package hr.tvz.groops.service;

import hr.tvz.groops.command.crud.CommentCommand;
import hr.tvz.groops.criteria.Searchable;
import hr.tvz.groops.dto.response.CommentDto;
import hr.tvz.groops.exception.UnauthorizedException;
import hr.tvz.groops.model.Comment;
import hr.tvz.groops.model.Group;
import hr.tvz.groops.model.Post;
import hr.tvz.groops.model.User;
import hr.tvz.groops.model.enums.EntityTypeEnum;
import hr.tvz.groops.model.enums.PermissionEnum;
import hr.tvz.groops.repository.CommentRepository;
import hr.tvz.groops.repository.PostRepository;
import hr.tvz.groops.repository.UserRepository;
import hr.tvz.groops.service.security.AuthenticationService;
import hr.tvz.groops.service.security.AuthorizationService;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static hr.tvz.groops.util.TimeUtils.now;

@Service
public class CommentService implements Searchable {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private final ModelMapper modelMapper;
    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(ModelMapper modelMapper,
                          AuthenticationService authenticationService,
                          AuthorizationService authorizationService,
                          UserRepository userRepository,
                          PostRepository postRepository,
                          NotificationService notificationService, CommentRepository commentRepository) {
        this.modelMapper = modelMapper;
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.notificationService = notificationService;
        this.commentRepository = commentRepository;
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public CommentDto create(@NotNull CommentCommand command, @NotNull Long postId) {
        logger.debug("Creating comment for post with id: {}", postId);
        Instant now = now();
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User user = findUserEntityById(currentUserId, userRepository);
        Post post = findPostById(postId, postRepository);
        Group group = post.getGroup();
        authorizationService.hasGroupPermission(user, group, PermissionEnum.WRITE_COMMENT);
        Comment comment = Comment.builder()
                .text(command.getText())
                .post(post)
                .user(user)
                .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                .createdTs(now)
                .build();
        notificationService.sendNotificationToUser(post.getUser(),
                "User " + user.getUsername() + " commented on your post in group " + group.getName(),
                post.getId(),
                EntityTypeEnum.COMMENT,
                group.getId()
        );
        return modelMapper.map(commentRepository.saveAndFlush(comment), CommentDto.class);
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public CommentDto update(@NotNull CommentCommand command, @NotNull Long commentId) {
        logger.debug("Updating comment for with id: {}", commentId);
        Instant now = now();
        Comment comment = findCommentEntityById(commentId, commentRepository);
        validateWrite(comment);
        comment.setText(command.getText());
        comment.setModifiedBy(authenticationService.getCurrentLoggedInUserUsername());
        comment.setModifiedTs(now);
        return modelMapper.map(commentRepository.saveAndFlush(comment), CommentDto.class);
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public void delete(@NotNull Long commentId) {
        logger.debug("Deleting comment for with id: {}", commentId);
        Comment comment = findCommentEntityById(commentId, commentRepository);
        validateWrite(comment);
        commentRepository.delete(comment);
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT, isolation = Isolation.REPEATABLE_READ)
    public List<CommentDto> findAllByPostId(@NotNull Long postId) {
        logger.debug("Searching for comments for post with id: {}", postId);
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User user = findUserEntityById(currentUserId, userRepository);
        Post post = findPostById(postId, postRepository);
        Group group = post.getGroup();
        authorizationService.hasGroupPermission(user, group, PermissionEnum.READ_COMMENT);
        return commentRepository.findAllByPost(post).stream()
                .map((element) -> modelMapper.map(element, CommentDto.class))
                .collect(Collectors.toList());
    }

    private void validateWrite(Comment comment) {
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User user = findUserEntityById(currentUserId, userRepository);
        if (comment.getUser().getId().compareTo(currentUserId) != 0) {
            throw new UnauthorizedException("Unauthorized");
        }
        authorizationService.hasGroupPermission(user, comment.getPost().getGroup(), PermissionEnum.WRITE_COMMENT);
    }

}
