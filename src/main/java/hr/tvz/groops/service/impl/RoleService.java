package hr.tvz.groops.service.impl;

import com.querydsl.core.BooleanBuilder;
import hr.tvz.groops.command.crud.RoleCommand;
import hr.tvz.groops.command.search.RoleSearchCommand;
import hr.tvz.groops.dto.response.RoleDto;
import hr.tvz.groops.model.Permission;
import hr.tvz.groops.model.QRole;
import hr.tvz.groops.model.Role;
import hr.tvz.groops.model.RolePermission;
import hr.tvz.groops.model.pk.RolePermissionId;
import hr.tvz.groops.repository.PermissionRepository;
import hr.tvz.groops.repository.RolePermissionRepository;
import hr.tvz.groops.repository.RoleRepository;
import hr.tvz.groops.service.Searchable;
import hr.tvz.groops.util.QueryBuilderUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static hr.tvz.groops.constants.CrudExceptionEnum.ROLE_PERMISSION_NOT_FOUND_BY_ROLE_ID_AND_PERMISSION_ID;
import static hr.tvz.groops.util.MapUtil.commandToEntity;
import static hr.tvz.groops.util.TimeUtils.now;

@Service
public class RoleService implements Searchable {
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final AuthenticationService authenticationService;

    @Autowired
    public RoleService(ModelMapper modelMapper, RoleRepository roleRepository, PermissionRepository permissionRepository, RolePermissionRepository rolePermissionRepository, AuthenticationService authenticationService) {
        this.modelMapper = modelMapper;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.authenticationService = authenticationService;
    }

    @Transactional
    public RoleDto create(RoleCommand command) {
        logger.debug("Creating role...");
        Instant now = now();
        Role role = commandToEntity(command, new Role());
        role.setCreatedBy(authenticationService.getCurrentLoggedInUserUsername());
        role.setCreatedTs(now);
        return modelMapper.map(roleRepository.save(role), RoleDto.class);
    }

    @Transactional
    public void addPermissionToRole(Long roleId, Long permissionId) {
        logger.debug("Adding permission with id {} to role with id {}", permissionId, roleId);
        Instant now = now();
        Role role = findRoleEntityById(roleId, roleRepository);
        Permission permission = findPermissionEntityById(permissionId, permissionRepository);
        RolePermissionId rolePermissionId = new RolePermissionId();
        rolePermissionId.setPermissionId(permission.getId());
        rolePermissionId.setRoleId(role.getId());
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRolePermissionId(rolePermissionId);
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        rolePermission.setCreatedBy(authenticationService.getCurrentLoggedInUserUsername());
        rolePermission.setCreatedTs(now);
        rolePermissionRepository.save(rolePermission);
    }

    @Transactional
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        logger.debug("Removing permission with id {} to role with id {}", permissionId, roleId);
        RolePermission rolePermission = rolePermissionRepository.findByRoleIdAndPermissionId(roleId, permissionId).orElseThrow(() -> new EntityNotFoundException(ROLE_PERMISSION_NOT_FOUND_BY_ROLE_ID_AND_PERMISSION_ID.getMessageComposed(roleId, permissionId)));
        rolePermissionRepository.delete(rolePermission);
    }

    @Transactional
    public RoleDto update(RoleCommand command, Long id) {
        logger.debug("Updating role by id {}", id);
        Instant now = now();
        Role role = findRoleEntityById(id, roleRepository);
        commandToEntity(command, role);
        role.setModifiedBy(authenticationService.getCurrentLoggedInUserUsername());
        role.setModifiedTs(now);
        return modelMapper.map(roleRepository.save(role), RoleDto.class);
    }

    public RoleDto findById(Long id) {
        return modelMapper.map(findRoleEntityById(id, roleRepository), RoleDto.class);
    }

    public List<RoleDto> findAll() {
        return roleRepository.findAll()
                .stream()
                .map(r -> modelMapper.map(r, RoleDto.class))
                .collect(Collectors.toList());
    }

    public Page<RoleDto> search(RoleSearchCommand command, Pageable pageable) {
        QRole role = QRole.role1;
        BooleanBuilder builder = new BooleanBuilder();
        QueryBuilderUtil.buildCreatedModifiedAndIdConditions(command, role._super, role.id, builder);
        QueryBuilderUtil.equalsEnum(builder, role.role, command.getRole());
        return roleRepository.findAll(builder.getValue() != null ? builder.getValue() : builder, pageable)
                .map(u -> modelMapper.map(u, RoleDto.class));
    }

    @Transactional
    public void deleteById(Long id) {
        logger.debug("Deleting role by id {}", id);
        Role role = findRoleEntityById(id, roleRepository);
        roleRepository.delete(role);
    }

}
