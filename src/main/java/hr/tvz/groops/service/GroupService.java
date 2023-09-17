package hr.tvz.groops.service;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import com.querydsl.core.BooleanBuilder;
import hr.tvz.groops.command.crud.GroupCommand;
import hr.tvz.groops.command.search.GroupSearchCommand;
import hr.tvz.groops.command.searchPaginated.GroupPaginatedSearchCommand;
import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.criteria.Searchable;
import hr.tvz.groops.dto.response.*;
import hr.tvz.groops.exception.InternalServerException;
import hr.tvz.groops.model.*;
import hr.tvz.groops.model.enums.EntityTypeEnum;
import hr.tvz.groops.model.enums.PermissionEnum;
import hr.tvz.groops.model.enums.RoleEnum;
import hr.tvz.groops.model.pk.GroupRequestId;
import hr.tvz.groops.model.pk.UserGroupRoleId;
import hr.tvz.groops.repository.*;
import hr.tvz.groops.service.s3.S3Service;
import hr.tvz.groops.service.security.AuthenticationService;
import hr.tvz.groops.service.security.AuthorizationService;
import hr.tvz.groops.util.QueryBuilderUtil;
import hr.tvz.groops.util.UploadUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static hr.tvz.groops.util.TimeUtils.now;

@Service
public class GroupService implements Searchable {
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AuthenticationService authenticationService;
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupRequestRepository groupRequestRepository;
    private final UserRepository userRepository;
    private final UserGroupRoleRepository userGroupRoleRepository;
    private final S3Service s3Service;
    private final AuthorizationService authorizationService;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;
    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public GroupService(SimpMessagingTemplate simpMessagingTemplate, AuthenticationService authenticationService,
                        GroupRepository groupRepository,
                        UserGroupRepository userGroupRepository,
                        GroupRequestRepository groupRequestRepository,
                        UserRepository userRepository,
                        UserGroupRoleRepository userGroupRoleRepository,
                        S3Service s3Service,
                        AuthorizationService authorizationService,
                        NotificationService notificationService, ModelMapper modelMapper,
                        EntityManager entityManager) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.authenticationService = authenticationService;
        this.groupRepository = groupRepository;
        this.userGroupRepository = userGroupRepository;
        this.groupRequestRepository = groupRequestRepository;
        this.userRepository = userRepository;
        this.userGroupRoleRepository = userGroupRoleRepository;
        this.s3Service = s3Service;
        this.authorizationService = authorizationService;
        this.notificationService = notificationService;
        this.modelMapper = modelMapper;
        this.entityManager = entityManager;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, timeout = TimeoutConstants.SHORT_TIMEOUT)
    public GroupDto findById(Long id) {
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User user = findUserEntityById(currentUserId, userRepository);
        Group group = findGroupById(id, groupRepository);
        boolean sentJoin = groupRequestRepository.existsByGroupAndUser(group, user);
        GroupDto groupDto = modelMapper.map(group, GroupDto.class);
        groupDto.setSentJoin(sentJoin);
        return groupDto;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, timeout = TimeoutConstants.SHORT_TIMEOUT)
    public GroupRoleDto findByGroupIdForCurrentUser(Long groupId) {
        User user = findUserEntityById(authenticationService.getCurrentLoggedInUserId(), userRepository);
        Group group = findGroupById(groupId, groupRepository);
        UserGroup userGroup = findUserGroupByUserAndGroup(user, group, userGroupRepository);
        List<UserGroupRole> userGroupRoles = userGroupRoleRepository.findByUserGroup(userGroup);
        List<RoleDto> roles = userGroupRoles.stream()
                .map(ugr -> modelMapper.map(ugr.getRole(), RoleDto.class))
                .collect(Collectors.toList());
        return GroupRoleDto.builder()
                .groupId(groupId)
                .roles(roles)
                .build();
    }

    public List<GroupDto> findAll() {
        logger.debug("Fetching all groups");
        return groupRepository.findAll().stream().map(g -> modelMapper.map(g, GroupDto.class)).collect(Collectors.toList());
    }

    @Transactional(timeout = TimeoutConstants.LONG_TIMEOUT)
    public GroupDto create(GroupCommand command, @Nullable MultipartFile file) {
        logger.debug("Creating group: {}", command.getName());
        Instant now = now();
        Group group = Group.builder()
                .name(command.getName())
                .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                .createdTs(now)
                .build();
        group = groupRepository.saveAndFlush(group);

        User currentUser = findUserEntityByIdLockByPessimisticWrite(authenticationService.getCurrentLoggedInUserId(), userRepository);
        UserGroup userGroup = UserGroup.builder()
                .user(currentUser)
                .group(group)
                .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                .createdTs(now)
                .build();
        userGroup = userGroupRepository.saveAndFlush(userGroup);

        Role adminRole = authorizationService.getOrCreateAdminRole(now);
        UserGroupRoleId userGroupRoleId = new UserGroupRoleId();
        userGroupRoleId.setUserGroupId(userGroup.getId());
        userGroupRoleId.setRoleId(adminRole.getId());
        UserGroupRole userGroupRole = UserGroupRole.builder()
                .userGroupRoleId(userGroupRoleId)
                .userGroup(userGroup)
                .role(adminRole)
                .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                .createdTs(now)
                .build();
        userGroupRoleRepository.saveAndFlush(userGroupRole);

        if (file != null) {
            uploadProfilePictureCompressed(group, file);
        }

        return modelMapper.map(group, GroupDto.class);
    }

    @Transactional(timeout = TimeoutConstants.LONG_TIMEOUT, propagation = Propagation.MANDATORY)
    public void uploadProfilePictureCompressed(@NotNull Group group, @NotNull MultipartFile file) {
        UploadUtil.checkIfImage(file);
        String newProfilePictureKey = s3Service.generateGroupProfilePictureKey(group.getId(), file);
        String newProfilePictureThumbnailKey = s3Service.generateGroupProfilePictureThumbnailKey(group.getId(), file);
        group.setProfilePictureKey(newProfilePictureKey);
        group.setProfilePictureThumbnailKey(newProfilePictureThumbnailKey);
        s3Service.uploadImageAndThumbnailCompressed(group.getProfilePictureKey(), group.getProfilePictureThumbnailKey(), file);
    }

    @Transactional(timeout = TimeoutConstants.LONG_TIMEOUT)
    public GroupDto update(Long id, GroupCommand command, MultipartFile file) {
        logger.debug("Updating group with id: {}", id);
        Instant now = now();
        User currentUser = findUserEntityByIdLockByPessimisticWrite(authenticationService.getCurrentLoggedInUserId(), userRepository);
        Group group = findGroupByIdLockByPessimisticWrite(id, groupRepository);
        authorizationService.hasGroupRole(currentUser, group, RoleEnum.ROLE_ADMIN);
        group.setName(command.getName());
        group.setModifiedBy(authenticationService.getCurrentLoggedInUserUsername());
        group.setModifiedTs(now);
        if (file != null) {
            uploadProfilePictureCompressed(group, file);
        }
        return modelMapper.map(group, GroupDto.class);
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT)
    public List<UserDto> findRequestsForJoin(Long groupId) {
        logger.debug("Finding join requests for group with id: {}", groupId);
        User currentUser = findUserEntityByIdLockByPessimisticWrite(authenticationService.getCurrentLoggedInUserId(), userRepository);
        Group group = findGroupByIdLockByPessimisticWrite(groupId, groupRepository);
        authorizationService.hasGroupRole(currentUser, group, RoleEnum.ROLE_ADMIN);
        List<GroupRequest> groupRequests = groupRequestRepository.findByGroup(group);
        return groupRequests.stream().map(gr -> modelMapper.map(gr.getUser(), UserDto.class)).collect(Collectors.toList());
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT, isolation = Isolation.REPEATABLE_READ)
    public List<UserRoleDto> findMembersByGroupId(Long groupId) {
        logger.debug("Finding join requests for group with id: {}", groupId);
        User currentUser = findUserEntityByIdLockByPessimisticWrite(authenticationService.getCurrentLoggedInUserId(), userRepository);
        Group group = findGroupByIdLockByPessimisticWrite(groupId, groupRepository);
        authorizationService.hasGroupPermission(currentUser, group, PermissionEnum.READ_MEMBERS);
        List<UserGroupRole> members = userGroupRoleRepository.findByGroup(group);
        return members.stream().map(m -> {
            User user = m.getUserGroup().getUser();
            RoleEnum role = m.getRole().getRole();
            UserRoleDto userRoleDto = modelMapper.map(user, UserRoleDto.class);
            userRoleDto.setRole(role);
            return userRoleDto;
        }).collect(Collectors.toList());
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT)
    public void acceptGroupRequest(Long userId, Long groupId, RoleEnum roleEnum) {
        logger.debug("Adding user with id: {} to group with id: {}", userId, groupId);
        Instant now = now();
        User currentUser = findUserEntityByIdLockByPessimisticWrite(authenticationService.getCurrentLoggedInUserId(), userRepository);
        User user = findUserEntityByIdLockByPessimisticWrite(userId, userRepository);
        Group group = findGroupByIdLockByPessimisticWrite(groupId, groupRepository);
        authorizationService.hasGroupRole(currentUser, group, RoleEnum.ROLE_ADMIN);
        GroupRequest groupRequest = findGroupRequestByGroupAndUser(group, user, groupRequestRepository);
        groupRequestRepository.delete(groupRequest);

        UserGroup userGroup = UserGroup.builder()
                .user(user)
                .group(group)
                .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                .createdTs(now)
                .build();
        userGroup = userGroupRepository.saveAndFlush(userGroup);

        UserGroupRole userGroupRole = UserGroupRole.builder()
                .userGroup(userGroup)
                .createdTs(now)
                .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                .build();

        Role role;
        switch (roleEnum) {
            case ROLE_LURKER:
                role = authorizationService.getOrCreateLurkerRole(now);
                break;
            case ROLE_USER:
                role = authorizationService.getOrCreateUserRole(now);
                break;
            case ROLE_ADMIN:
                role = authorizationService.getOrCreateAdminRole(now);
                break;
            default:
                throw new InternalServerException("Role not supported");
        }

        userGroupRole.setRole(role);
        UserGroupRoleId userGroupRoleId = new UserGroupRoleId();
        userGroupRoleId.setUserGroupId(userGroup.getId());
        userGroupRoleId.setRoleId(role.getId());
        userGroupRole.setUserGroupRoleId(userGroupRoleId);
        userGroupRoleRepository.saveAndFlush(userGroupRole);
        notificationService.sendNotificationToUser(user,
                "You have been added to group " + group.getName() + " with role " + role.getRole().name().replaceFirst("ROLE_", ""),
                group.getId(),
                EntityTypeEnum.GROUP_ACCEPT
        );
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT)
    public void rejectGroupRequest(Long userId, Long groupId) {
        logger.debug("Rejecting group join request from user with id: {} for group with id: {}", userId, groupId);
        User currentUser = findUserEntityByIdLockByPessimisticWrite(authenticationService.getCurrentLoggedInUserId(), userRepository);
        User user = findUserEntityByIdLockByPessimisticWrite(userId, userRepository);
        Group group = findGroupByIdLockByPessimisticWrite(groupId, groupRepository);
        authorizationService.hasGroupRole(currentUser, group, RoleEnum.ROLE_ADMIN);
        GroupRequest groupRequest = findGroupRequestByGroupAndUser(group, user, groupRequestRepository);
        groupRequestRepository.delete(groupRequest);
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT)
    public void sendGroupJoinRequest(Long groupId) {
        logger.debug("Sending request from user with id: {} to join group with id: {}", authenticationService.getCurrentLoggedInUserId(), groupId);
        Instant now = now();
        User user = findUserEntityById(authenticationService.getCurrentLoggedInUserId(), userRepository);
        Group group = findGroupByIdLockByPessimisticWrite(groupId, groupRepository);
        if (userGroupRepository.existsByUserAndGroup(user, group)) {
            throw new IllegalArgumentException("Can't request to join group you are already a part of");
        }
        GroupRequestId groupRequestId = new GroupRequestId();
        groupRequestId.setGroupId(group.getId());
        groupRequestId.setUserId(user.getId());
        GroupRequest groupRequest = GroupRequest.builder()
                .groupRequestId(groupRequestId)
                .user(user)
                .group(group)
                .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                .createdTs(now)
                .build();
        groupRequestRepository.save(groupRequest);
        Set<User> admins = userGroupRepository.findUsersByGroupAndRole(group, RoleEnum.ROLE_ADMIN);
        for (User admin : admins) {
            notificationService.sendNotificationToUser(admin,
                    "User " + user.getUsername() + " requested to join group " + group.getName(),
                    group.getId(),
                    EntityTypeEnum.GROUP_REQUEST
            );
        }
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT)
    public void cancelGroupJoinRequest(Long groupId) {
        logger.debug("Canceling request from user with id: {} to join group with id: {}", authenticationService.getCurrentLoggedInUserId(), groupId);
        User user = findUserEntityById(authenticationService.getCurrentLoggedInUserId(), userRepository);
        Group group = findGroupByIdLockByPessimisticWrite(groupId, groupRepository);
        if (userGroupRepository.existsByUserAndGroup(user, group)) {
            throw new IllegalArgumentException("Can't request to join group you are already a part of");
        }
        GroupRequest groupRequest = findGroupRequestByGroupAndUser(group, user, groupRequestRepository);
        groupRequestRepository.delete(groupRequest);
    }

    public Page<GroupDto> searchPaginated(GroupPaginatedSearchCommand command, Pageable pageable) {
        QGroup group = QGroup.group;
        BooleanBuilder builder = new BooleanBuilder();
        QueryBuilderUtil.buildCreatedModifiedAndIdConditions(command, group._super, group.id, builder);
        QueryBuilderUtil.like(builder, group.name, command.getName());
        if (command.getIsMember() != null && command.getIsMember().isPresent()) {
            QueryBuilderUtil.equals(builder, group.users.any().id, authenticationService.getCurrentLoggedInUserId());
        }
        return groupRepository.findAll(builder.getValue() != null ? builder.getValue() : builder, pageable).map(u -> modelMapper.map(u, GroupDto.class));
    }

    public List<GroupDto> search(GroupSearchCommand command) {
        User currentUser = findUserEntityById(authenticationService.getCurrentLoggedInUserId(), userRepository);
        boolean my = command.getMy() != null && command.getMy().get();
        boolean name = command.getName() != null && command.getName().isPresent() && !command.getName().get().trim().isBlank();
        CriteriaBuilderConfiguration config = Criteria.getDefault();
        CriteriaBuilderFactory cbf = config.createCriteriaBuilderFactory(entityManager.getEntityManagerFactory());
        QGroup group = QGroup.group;
        BooleanBuilder builder = new BooleanBuilder();
        if (name) {
            QueryBuilderUtil.like(builder, group.name, command.getName());
        }
        if (my) {
            QueryBuilderUtil.equals(builder, group.users.any().id, authenticationService.getCurrentLoggedInUserId());
        }
        BlazeJPAQuery<Group> query = new BlazeJPAQuery<>(entityManager, cbf)
                .select(group)
                .from(group)
                .where(builder);
        List<Group> groups = query.fetch();
        if (my) {
            return groups.stream().map(u -> {
                GroupDto g = modelMapper.map(u, GroupDto.class);
                g.setMy(true);
                return g;
            }).collect(Collectors.toList());
        }
        Set<Long> memberIds = new HashSet<>();
        Set<Long> sentJoinIds = new HashSet<>();
        for (Group g : groups) {
            if (userGroupRepository.existsByUserAndGroup(currentUser, g)) {
                memberIds.add(g.getId());
            }
            if (groupRequestRepository.existsByGroupAndUser(g, currentUser)) {
                sentJoinIds.add(g.getId());
            }
        }
        return groups.stream().map(u -> {
            GroupDto g = modelMapper.map(u, GroupDto.class);
            g.setMy(memberIds.contains(g.getId()));
            g.setSentJoin(sentJoinIds.contains(g.getId()));
            return g;
        }).collect(Collectors.toList());
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT)
    public void deleteById(Long groupId) {
        logger.info("Deleting group with id: {}", groupId);
        User currentUser = findUserEntityByIdLockByPessimisticWrite(authenticationService.getCurrentLoggedInUserId(), userRepository);
        Group group = findGroupByIdLockByPessimisticWrite(groupId, groupRepository);
        authorizationService.hasGroupRole(currentUser, group, RoleEnum.ROLE_ADMIN);
        groupRepository.delete(group);
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT)
    public void kickUser(Long groupId, Long userId) {
        Instant now = now();
        User currentUser = findUserEntityByIdLockByPessimisticWrite(authenticationService.getCurrentLoggedInUserId(), userRepository);
        User user = findUserEntityByIdLockByPessimisticWrite(userId, userRepository);
        Group group = findGroupByIdLockByPessimisticWrite(groupId, groupRepository);
        authorizationService.hasGroupRole(currentUser, group, RoleEnum.ROLE_ADMIN);
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        UserGroup userGroup = findUserGroupByUserAndGroup(user, group, userGroupRepository);
        Role adminRole = authorizationService.getOrCreateAdminRole(now);

        if (user.getId().compareTo(currentUserId) == 0) {
            throw new IllegalArgumentException("Can't kick yourself out of the group");
        }

        if (userGroupRoleRepository.existsByUserGroupAndRole(userGroup, adminRole)) {
            throw new IllegalArgumentException("Can't kick admin from group");
        }

        logger.info("Kicked user with id: {} from group", user.getId());
        userGroupRepository.delete(userGroup);
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT, isolation = Isolation.REPEATABLE_READ)
    public RoleEnum getCurrentUserRoleByGroupId(Long groupId) {
        User currentUser = findUserEntityByIdLockByPessimisticWrite(authenticationService.getCurrentLoggedInUserId(), userRepository);
        Group group = findGroupByIdLockByPessimisticWrite(groupId, groupRepository);
        UserGroup userGroup = findUserGroupByUserAndGroup(currentUser, group, userGroupRepository);
        List<UserGroupRole> userGroupRoles = userGroupRoleRepository.findByUserGroup(userGroup);
        if (userGroupRoles.isEmpty()) {
            throw new IllegalArgumentException("User group role doesn't exist for current user");
        }
        return userGroupRoles.get(0).getRole().getRole();
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT)
    public void changeUserRole(Long groupId, Long userId, RoleEnum roleEnum) {
        logger.debug("Removing user with id: {} from group with id: {}", userId, groupId);
        Instant now = now();
        User currentUser = findUserEntityByIdLockByPessimisticWrite(authenticationService.getCurrentLoggedInUserId(), userRepository);
        User user = findUserEntityByIdLockByPessimisticWrite(userId, userRepository);
        Group group = findGroupByIdLockByPessimisticWrite(groupId, groupRepository);
        authorizationService.hasGroupRole(currentUser, group, RoleEnum.ROLE_ADMIN);
        if (userId.compareTo(authenticationService.getCurrentLoggedInUserId()) == 0) {
            throw new IllegalArgumentException("Can't change your own role");
        }
        UserGroup userGroup = findUserGroupByUserAndGroup(user, group, userGroupRepository);
        List<UserGroupRole> userGroupRoles = userGroupRoleRepository.findByUserGroup(userGroup);
        userGroupRoleRepository.deleteAll(userGroupRoles);
        Role role = authorizationService.getOrCreateByRoleEnum(roleEnum, now);
        UserGroupRoleId userGroupRoleId = new UserGroupRoleId();
        userGroupRoleId.setUserGroupId(userGroup.getId());
        userGroupRoleId.setRoleId(role.getId());
        UserGroupRole newUserGroupRole = UserGroupRole.builder()
                .userGroupRoleId(userGroupRoleId)
                .userGroup(userGroup)
                .role(role)
                .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                .createdTs(now)
                .build();
        userGroupRoleRepository.save(newUserGroupRole);
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT)
    public void leaveGroup(Long groupId) {
        logger.info("User with id: {} is leaving group...", authenticationService.getCurrentLoggedInUserId());
        Instant now = now();
        User currentUser = findUserEntityByIdLockByPessimisticWrite(authenticationService.getCurrentLoggedInUserId(), userRepository);
        Group group = findGroupByIdLockByPessimisticWrite(groupId, groupRepository);
        Role adminRole = authorizationService.getOrCreateAdminRole(now);
        UserGroup userGroup = findUserGroupByUserAndGroup(currentUser, group, userGroupRepository);

        if (userGroupRoleRepository.existsByUserGroupAndRole(userGroup, adminRole) && userGroupRoleRepository.countAllByGroupAndRole(group, adminRole) == 1) {
            throw new IllegalArgumentException("Can't leave group because you are the only admin");
        }

        userGroupRepository.delete(userGroup);
    }


}


