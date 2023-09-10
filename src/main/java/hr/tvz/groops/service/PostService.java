package hr.tvz.groops.service;

import com.querydsl.core.BooleanBuilder;
import hr.tvz.groops.command.crud.PostCommand;
import hr.tvz.groops.command.searchPaginated.PostPaginatedSearchCommand;
import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.criteria.Searchable;
import hr.tvz.groops.dto.response.PostDto;
import hr.tvz.groops.exception.UnauthorizedException;
import hr.tvz.groops.model.*;
import hr.tvz.groops.model.enums.PermissionEnum;
import hr.tvz.groops.model.pk.PostLikeId;
import hr.tvz.groops.repository.*;
import hr.tvz.groops.service.s3.S3Service;
import hr.tvz.groops.service.security.AuthenticationService;
import hr.tvz.groops.service.security.AuthorizationService;
import hr.tvz.groops.util.QueryBuilderUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.Instant;
import java.util.Objects;

import static hr.tvz.groops.util.TimeUtils.now;

@Service
public class PostService implements Searchable {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;
    private final S3Service s3Service;
    private final PostRepository postRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PostService(AuthenticationService authenticationService,
                       AuthorizationService authorizationService,
                       S3Service s3Service,
                       PostRepository postRepository,
                       GroupRepository groupRepository,
                       UserRepository userRepository,
                       PostLikeRepository postLikeRepository,
                       CommentRepository commentRepository,
                       ModelMapper modelMapper) {
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
        this.s3Service = s3Service;
        this.postRepository = postRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.postLikeRepository = postLikeRepository;
        this.commentRepository = commentRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public PostDto findById(Long id) {
        User currentUser = findUserEntityById(authenticationService.getCurrentLoggedInUserId(), userRepository);
        Post post = findPostById(id, postRepository);
        Group group = post.getGroup();
        authorizationService.hasGroupPermission(currentUser, group, PermissionEnum.READ_COMMENT);
        return mapLikes(post, currentUser);
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public PostDto create(@NotNull @Valid PostCommand command, @NotNull Long groupId) {
        return create(command, groupId, null);
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public PostDto create(@NotNull @Valid PostCommand command, @NotNull Long groupId, @Nullable MultipartFile file) {
        logger.debug("Creating post for group id: {}", groupId);
        Instant now = now();
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User currentUser = findUserEntityByIdLockByPessimisticWrite(currentUserId, userRepository);
        Group group = findGroupById(groupId, groupRepository);
        authorizationService.hasGroupPermission(currentUser, group, PermissionEnum.WRITE_POST);

        Post post = Post.builder()
                .text(command.getText())
                .user(currentUser)
                .group(group)
                .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                .createdTs(now)
                .build();

        post = postRepository.saveAndFlush(post);

        if (file == null) {
            return modelMapper.map(post, PostDto.class);
        }

        String key = s3Service.generatePostPictureKey(post.getId(), file);
        post.setMediaKey(key);
        post.setModifiedBy(authenticationService.getCurrentLoggedInUserUsername());
        post.setModifiedTs(now);
        post = postRepository.saveAndFlush(post);

        s3Service.uploadDocumentFull(key, file);
        return modelMapper.map(post, PostDto.class);
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public PostDto update(@NotNull @Valid PostCommand command, @NotNull Long postId, @NotNull MultipartFile file) {
        return update(command, postId, file, false);
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public PostDto update(@NotNull @Valid PostCommand command, @NotNull Long postId, @NotNull Boolean removeMedia) {
        return update(command, postId, null, removeMedia);
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public PostDto update(@NotNull @Valid PostCommand command, @NotNull Long postId, @Nullable MultipartFile file, @NotNull Boolean removeMedia) {
        logger.debug("Updating post with id: {}", postId);
        Instant now = now();
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User currentUser = findUserEntityByIdLockByPessimisticWrite(currentUserId, userRepository);
        Post post = findPostById(postId, postRepository);

        if (post.getUser().getId().compareTo(currentUser.getId()) != 0) {
            throw new UnauthorizedException("Unauthorized");
        }
        Group group = post.getGroup();
        authorizationService.hasGroupPermission(currentUser, group, PermissionEnum.WRITE_POST);

        post.setText(command.getText());
        post.setModifiedBy(authenticationService.getCurrentLoggedInUserUsername());
        post.setModifiedTs(now);

        if (file == null) {
            if (!removeMedia) {
                post = postRepository.saveAndFlush(post);
                return mapLikes(post, currentUser);
            }
            logger.debug("Removing media");
            String oldKey = post.getMediaKey();
            post.setMediaKey(null);
            post = postRepository.saveAndFlush(post);
            s3Service.deleteByKey(oldKey);
            return mapLikes(post, currentUser);
        }

        logger.debug("Uploading new media");
        String oldMediaKey = post.getMediaKey();
        String newMediaKey = s3Service.generatePostPictureKey(post.getId(), file);
        post.setMediaKey(newMediaKey);
        post = postRepository.saveAndFlush(post);
        if (oldMediaKey != null) {
            logger.debug("Deleting existing media with key: {}", oldMediaKey);
            s3Service.deleteByKey(oldMediaKey);
        }
        s3Service.uploadDocumentFull(post.getMediaKey(), file);
        return mapLikes(post, currentUser);
    }

    private PostDto mapLikes(Post post, User user) {
        PostDto postDto = modelMapper.map(post, PostDto.class);
        Integer likeCount = postLikeRepository.countAllByPost(post);
        Boolean youLike = postLikeRepository.existsByPostAndUser(post, user);
        Integer commentCount = commentRepository.countAllByPost(post);
        postDto.setLikeCount(likeCount);
        postDto.setYouLike(youLike);
        postDto.setCommentCount(commentCount);
        return postDto;
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public void delete(Long postId) {
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User currentUser = findUserEntityByIdLockByPessimisticWrite(currentUserId, userRepository);
        Post post = findPostById(postId, postRepository);

        if (post.getUser().getId().compareTo(currentUser.getId()) != 0) {
            throw new UnauthorizedException("Unauthorized");
        }
        authorizationService.hasGroupPermission(currentUser, post.getGroup(), PermissionEnum.WRITE_POST);

        logger.debug("Deleting post with id: {}", postId);
        postRepository.delete(post);
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT, isolation = Isolation.REPEATABLE_READ)
    public Page<PostDto> search(PostPaginatedSearchCommand command, Pageable pageable) {
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        QPost post = QPost.post;
        User currentUser = findUserEntityById(authenticationService.getCurrentLoggedInUserId(), userRepository);
        BooleanBuilder builder = new BooleanBuilder();
        QueryBuilderUtil.buildCreatedModifiedAndIdConditions(command, post._super, post.id, builder);
        QueryBuilderUtil.like(builder, post.text, command.getText());
        QueryBuilderUtil.equals(builder, post.user.id, command.getUserId());
        QueryBuilderUtil.equals(builder, post.group.id, command.getGroupId());
        QueryBuilderUtil.equals(builder, post.group.users.any().id, currentUserId);

        return postRepository.findAll(builder.getValue() != null ? builder.getValue() : builder, pageable)
                .map(p -> mapLikes(p, currentUser));
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public void like(Long postId) {
        logger.debug("Liking post with id: {}", postId);
        Instant now = now();
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User currentUser = findUserEntityByIdLockByPessimisticWrite(currentUserId, userRepository);
        Post post = findPostById(postId, postRepository);
        Group group = post.getGroup();
        authorizationService.hasGroupPermission(currentUser, group, PermissionEnum.LIKE_POST);

        PostLikeId postLikeId = new PostLikeId();
        postLikeId.setPostId(post.getId());
        postLikeId.setUserId(currentUser.getId());
        PostLike postLike = PostLike.builder()
                .postLikeId(postLikeId)
                .post(post)
                .user(currentUser)
                .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                .createdTs(now)
                .build();
        postLikeRepository.save(postLike);
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public void unlike(Long postId) {
        logger.debug("Unliking post with id: {}", postId);
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User currentUser = findUserEntityByIdLockByPessimisticWrite(currentUserId, userRepository);
        Post post = findPostById(postId, postRepository);
        PostLike postLike = findPostLikeByPostAndUser(post, currentUser, postLikeRepository);

        if (!Objects.equals(currentUser.getId(), postLike.getUser().getId())) {
            throw new UnauthorizedException("Unauthorized");
        }
        authorizationService.hasGroupPermission(currentUser, post.getGroup(), PermissionEnum.LIKE_POST);

        logger.debug("Deleting post like");
        postLikeRepository.delete(postLike);
    }
}
