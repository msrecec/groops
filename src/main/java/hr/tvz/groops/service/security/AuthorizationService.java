package hr.tvz.groops.service.security;

import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.criteria.Searchable;
import hr.tvz.groops.dto.response.GroupRoleDto;
import hr.tvz.groops.dto.response.PermissionDto;
import hr.tvz.groops.dto.response.RoleDto;
import hr.tvz.groops.exception.InternalServerException;
import hr.tvz.groops.exception.UnauthorizedException;
import hr.tvz.groops.model.*;
import hr.tvz.groops.model.enums.PermissionEnum;
import hr.tvz.groops.model.enums.RoleEnum;
import hr.tvz.groops.model.pk.RolePermissionId;
import hr.tvz.groops.repository.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static hr.tvz.groops.util.TimeUtils.now;

@Service
public class AuthorizationService implements Searchable {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionService permissionService;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserGroupRoleRepository userGroupRoleRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthorizationService(ModelMapper modelMapper,
                                RoleRepository roleRepository,
                                PermissionRepository permissionRepository,
                                PermissionService permissionService,
                                RolePermissionRepository rolePermissionRepository,
                                UserGroupRepository userGroupRepository,
                                UserGroupRoleRepository userGroupRoleRepository,
                                UserRepository userRepository,
                                GroupRepository groupRepository,
                                AuthenticationService authenticationService) {
        this.modelMapper = modelMapper;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.permissionService = permissionService;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userGroupRepository = userGroupRepository;
        this.userGroupRoleRepository = userGroupRoleRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.authenticationService = authenticationService;
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT, isolation = Isolation.REPEATABLE_READ)
    public GroupRoleDto findGroupRoles(Long groupId) {
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User currentUser = findUserEntityById(currentUserId, userRepository);
        Group group = findGroupById(groupId, groupRepository);
        UserGroup userGroup = findUserGroupByUserAndGroup(currentUser, group, userGroupRepository);
        List<UserGroupRole> userGroupRoles = userGroupRoleRepository.findByUserGroup(userGroup);
        List<RoleDto> roles = new ArrayList<>();
        for (UserGroupRole userGroupRole : userGroupRoles) {
            List<RolePermission> rolePermissions = rolePermissionRepository.findByRole(userGroupRole.getRole());
            RoleDto role = new RoleDto();
            role.setId(userGroupRole.getRole().getId());
            role.setRole(userGroupRole.getRole().getRole());
            List<Permission> permissions = rolePermissions.stream().map(RolePermission::getPermission).collect(Collectors.toList());
            role.setPermissions(permissions.stream().map(p -> modelMapper.map(p, PermissionDto.class)).collect(Collectors.toList()));
            roles.add(role);
        }
        return GroupRoleDto.builder()
                .groupId(group.getId())
                .roles(roles)
                .build();
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT, propagation = Propagation.MANDATORY)
    public void hasGroupRole(User user, Group group, RoleEnum roleEnum) {
        Instant now = now();
        UserGroup userGroup = findUserGroupByUserAndGroup(user, group, userGroupRepository);
        Role role = getOrCreateByRoleEnum(roleEnum, now);
        Optional<UserGroupRole> userGroupRole = userGroupRoleRepository.findByUserGroupAndRole(userGroup, role);
        if (userGroupRole.isEmpty()) {
            throw new AccessDeniedException("Unauthorized");
        }
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT, propagation = Propagation.MANDATORY)
    public void hasGroupPermission(User user, Group group, PermissionEnum permissionEnum) {
        Instant now = now();
        UserGroup userGroup = findUserGroupByUserAndGroup(user, group, userGroupRepository);
        Permission permission = permissionService.getOrCreatePermission(permissionEnum, now);
        List<UserGroupRole> userGroupRoles = userGroupRoleRepository.findByUserGroup(userGroup);
        for (UserGroupRole userGroupRole : userGroupRoles) {
            List<RolePermission> rolePermissions = rolePermissionRepository.findByRole(userGroupRole.getRole());
            for (RolePermission rolePermission : rolePermissions) {
                if (rolePermission.getPermission().getId().equals(permission.getId())) {
                    return;
                }
            }
        }
        throw new UnauthorizedException("Unauthorized");
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT, propagation = Propagation.MANDATORY)
    public Role getOrCreateAdminRole(Instant now) {
        return roleRepository.findRoleByRole(RoleEnum.ROLE_ADMIN).orElseGet(getAdminRoleSupplier(now));
    }

    private Supplier<Role> getAdminRoleSupplier(Instant now) {
        return () -> {
            RoleEnum roleAdmin = RoleEnum.ROLE_ADMIN;
            logger.info("Creating role: {}", roleAdmin.name());
            Role role = Role.builder()
                    .role(roleAdmin)
                    .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                    .createdTs(now)
                    .build();
            role = roleRepository.saveAndFlush(role);
            PermissionEnum[] permissionEnums = PermissionEnum.values();
            addPermissionsToRole(permissionEnums, role, now);
            return role;
        };
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT, propagation = Propagation.MANDATORY)
    public Role getOrCreateUserRole(Instant now) {
        return roleRepository.findRoleByRole(RoleEnum.ROLE_USER).orElseGet(getUserRoleSupplier(now));
    }

    private Supplier<Role> getUserRoleSupplier(Instant now) {
        RoleEnum roleUser = RoleEnum.ROLE_USER;
        return () -> {
            logger.info("Creating role: {}", roleUser.name());
            Role role = Role.builder()
                    .role(roleUser)
                    .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                    .createdTs(now)
                    .build();
            role = roleRepository.saveAndFlush(role);
            PermissionEnum[] permissionEnums = new PermissionEnum[]{
                    PermissionEnum.READ_COMMENT,
                    PermissionEnum.WRITE_COMMENT,
                    PermissionEnum.READ_POST,
                    PermissionEnum.WRITE_POST,
                    PermissionEnum.LIKE_POST,
                    PermissionEnum.WRITE_GROUP_MESSAGE,
                    PermissionEnum.READ_GROUP_MESSAGE,
                    PermissionEnum.READ_MEMBERS
            };
            addPermissionsToRole(permissionEnums, role, now);
            return role;
        };
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT, propagation = Propagation.MANDATORY)
    public Role getOrCreateLurkerRole(Instant now) {
        return roleRepository.findRoleByRole(RoleEnum.ROLE_LURKER).orElseGet(getLurkerRoleSupplier(now));
    }

    private Supplier<Role> getLurkerRoleSupplier(Instant now) {
        RoleEnum roleUser = RoleEnum.ROLE_LURKER;
        return () -> {
            logger.info("Creating role: {}", roleUser.name());
            Role role = Role.builder()
                    .role(roleUser)
                    .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                    .createdTs(now)
                    .build();
            role = roleRepository.saveAndFlush(role);
            PermissionEnum[] permissionEnums = new PermissionEnum[]{
                    PermissionEnum.READ_COMMENT,
                    PermissionEnum.READ_POST,
                    PermissionEnum.READ_GROUP_MESSAGE
            };
            addPermissionsToRole(permissionEnums, role, now);
            return role;
        };
    }

    private void addPermissionsToRole(PermissionEnum[] permissionEnums, Role role, Instant now) {
        for (PermissionEnum permissionEnum : permissionEnums) {
            Permission permission = permissionService.getOrCreatePermission(permissionEnum, now);
            RolePermissionId rolePermissionId = new RolePermissionId();
            rolePermissionId.setRoleId(role.getId());
            rolePermissionId.setPermissionId(permission.getId());
            RolePermission writeRolePermission = RolePermission.builder()
                    .rolePermissionId(rolePermissionId)
                    .role(role)
                    .permission(permission)
                    .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                    .createdTs(now)
                    .build();
            rolePermissionRepository.save(writeRolePermission);
        }
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT, propagation = Propagation.MANDATORY)
    public Role getOrCreateByRoleEnum(RoleEnum roleEnum, Instant now) {
        switch (roleEnum) {
            case ROLE_USER:
                return getOrCreateUserRole(now);
            case ROLE_ADMIN:
                return getOrCreateAdminRole(now);
            case ROLE_LURKER:
                return getOrCreateLurkerRole(now);
            default:
                throw new InternalServerException("Unsupported role");
        }
    }

}
