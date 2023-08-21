package hr.tvz.groops.service;

import com.querydsl.core.BooleanBuilder;
import hr.tvz.groops.command.crud.GroupCommand;
import hr.tvz.groops.command.search.GroupSearchCommand;
import hr.tvz.groops.command.search.UserSearchCommand;
import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.criteria.Searchable;
import hr.tvz.groops.dto.response.GroupDto;
import hr.tvz.groops.dto.response.UserDto;
import hr.tvz.groops.exception.InternalServerException;
import hr.tvz.groops.model.*;
import hr.tvz.groops.model.enums.RoleEnum;
import hr.tvz.groops.repository.*;
import hr.tvz.groops.util.QueryBuilderUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static hr.tvz.groops.util.TimeUtils.now;

@Service
public class GroupService implements Searchable {
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private final AuthenticationService authenticationService;
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupRequestRepository groupRequestRepository;
    private final UserRepository userRepository;
    private final UserGroupRoleRepository userGroupRoleRepository;
    private final AuthorizationService authorizationService;
    private final ModelMapper modelMapper;

    @Autowired
    public GroupService(AuthenticationService authenticationService,
                        GroupRepository groupRepository,
                        UserGroupRepository userGroupRepository,
                        GroupRequestRepository groupRequestRepository, UserRepository userRepository,
                        UserGroupRoleRepository userGroupRoleRepository,
                        AuthorizationService authorizationService,
                        ModelMapper modelMapper) {
        this.authenticationService = authenticationService;
        this.groupRepository = groupRepository;
        this.userGroupRepository = userGroupRepository;
        this.groupRequestRepository = groupRequestRepository;
        this.userRepository = userRepository;
        this.userGroupRoleRepository = userGroupRoleRepository;
        this.authorizationService = authorizationService;
        this.modelMapper = modelMapper;
    }

    public GroupDto findById(Long id) {
        return modelMapper.map(findGroupById(id, groupRepository), GroupDto.class);
    }

    public List<GroupDto> findAll() {
        logger.debug("Fetching all groups");
        return groupRepository.findAll().stream().map(g -> modelMapper.map(g, GroupDto.class)).collect(Collectors.toList());
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT)
    public GroupDto create(GroupCommand command) {
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
        UserGroupRole userGroupRole = UserGroupRole.builder()
                .userGroup(userGroup)
                .role(adminRole)
                .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                .createdTs(now)
                .build();
        userGroupRoleRepository.saveAndFlush(userGroupRole);

        return modelMapper.map(group, GroupDto.class);
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT)
    public GroupDto update(Long id, GroupCommand command) {
        logger.debug("Updating group with id: {}", id);
        Instant now = now();
        Group group = findGroupById(id, groupRepository);
        group.setName(command.getName());
        group.setModifiedBy(authenticationService.getCurrentLoggedInUserUsername());
        group.setModifiedTs(now);
        return modelMapper.map(group, GroupDto.class);
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT)
    public void acceptGroupRequest(Long userId, Long groupId, RoleEnum roleEnum) {
        logger.debug("Adding user with id: {} to group with id: {}", userId, groupId);
        Instant now = now();
        User user = findUserEntityByIdLockByPessimisticWrite(userId, userRepository);
        Group group = findGroupById(groupId, groupRepository);
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

        switch (roleEnum) {
            case ROLE_LURKER:
                userGroupRole.setRole(authorizationService.getOrCreateLurkerRole(now));
                break;
            case ROLE_USER:
                userGroupRole.setRole(authorizationService.getOrCreateUserRole(now));
                break;
            case ROLE_ADMIN:
                userGroupRole.setRole(authorizationService.getOrCreateAdminRole(now));
                break;
            default:
                throw new InternalServerException("Role not supported");
        }
        userGroupRoleRepository.saveAndFlush(userGroupRole);
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT)
    public void rejectGroupRequest(Long userId, Long groupId) {
        logger.debug("Rejecting group join request from user with id: {} for group with id: {}", userId, groupId);
        User user = findUserEntityByIdLockByPessimisticWrite(userId, userRepository);
        Group group = findGroupById(groupId, groupRepository);
        GroupRequest groupRequest = findGroupRequestByGroupAndUser(group, user, groupRequestRepository);
        groupRequestRepository.delete(groupRequest);
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT)
    public void sendGroupJoinRequest(Long groupId) {
        logger.debug("Sending request from user with id: {} to join group with id: {}", authenticationService.getCurrentLoggedInUserId(), groupId);
        Instant now = now();
        User user = findUserEntityById(authenticationService.getCurrentLoggedInUserId(), userRepository);
        Group group = findGroupById(groupId, groupRepository);
        if (userGroupRepository.existsByUserAndGroup(user, group)) {
            throw new IllegalArgumentException("Can't request to join group you are already a part of");
        }
        GroupRequest groupRequest = GroupRequest.builder()
                .user(user)
                .group(group)
                .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                .createdTs(now)
                .build();
        groupRequestRepository.save(groupRequest);
    }

    public Page<GroupDto> search(GroupSearchCommand command, Pageable pageable) {
        QGroup group = QGroup.group;
        BooleanBuilder builder = new BooleanBuilder();
        QueryBuilderUtil.buildCreatedModifiedAndIdConditions(command, group._super, group.id, builder);
        QueryBuilderUtil.like(builder, group.name, command.getName());
        if (command.getIsMember() != null && command.getIsMember().isPresent()) {
            QueryBuilderUtil.equals(builder, group.users.any().id, authenticationService.getCurrentLoggedInUserId());
        }
        return userRepository.findAll(builder.getValue() != null ? builder.getValue() : builder, pageable).map(u -> modelMapper.map(u, GroupDto.class));
    }

    // todo change user role in group
    // todo add leave group
    // todo add check if there are other admin users in case the user leaving the group is the only admin
    // todo can't remove other admins from group
    // todo maybe check to have another role: superadmin
    // todo if we are going to use superadmin, we should add ability to transfer credentials to other users

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT)
    public void deleteById(Long groupId) {
        logger.info("Deleting group with id: {}", groupId);
        Group group = findGroupById(groupId, groupRepository);
        groupRepository.delete(group);
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT)
    public void removeUserFromGroup(Long groupId, Long userId) {
        logger.debug("Removing user with id: {} from group with id: {}", userId, groupId);
        User user = findUserEntityByIdLockByPessimisticWrite(userId, userRepository);
        Group group = findGroupById(groupId, groupRepository);
        UserGroup userGroup = findUserGroupByUserAndGroup(user, group, userGroupRepository);
        userGroupRepository.delete(userGroup);
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT)
    public void changeRole(Long userId, Long groupId, RoleEnum roleEnum) {
        logger.debug("Removing user with id: {} from group with id: {}", userId, groupId);
        User user = findUserEntityByIdLockByPessimisticWrite(userId, userRepository);
        Group group = findGroupById(groupId, groupRepository);
        UserGroup userGroup = findUserGroupByUserAndGroup(user, group, userGroupRepository);
        UserGroupRole userGroupRole =
    }

}
