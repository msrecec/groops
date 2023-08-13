package hr.tvz.groops.service.impl;

import com.querydsl.core.BooleanBuilder;
import hr.tvz.groops.command.crud.PermissionCommand;
import hr.tvz.groops.command.search.PermissionSearchCommand;
import hr.tvz.groops.dto.response.PermissionDto;
import hr.tvz.groops.model.Permission;
import hr.tvz.groops.model.QPermission;
import hr.tvz.groops.repository.PermissionRepository;
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

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static hr.tvz.groops.util.MapUtil.commandToEntity;
import static hr.tvz.groops.util.TimeUtils.now;

@Service
public class PermissionService implements Searchable {
    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);
    private final ModelMapper modelMapper;
    private final PermissionRepository permissionRepository;
    private final AuthenticationService authenticationService;

    @Autowired
    public PermissionService(ModelMapper modelMapper, PermissionRepository permissionRepository, AuthenticationService authenticationService) {
        this.modelMapper = modelMapper;
        this.permissionRepository = permissionRepository;
        this.authenticationService = authenticationService;
    }

    @Transactional
    public PermissionDto create(PermissionCommand command) {
        logger.debug("Creating permission...");
        Instant now = now();
        Permission permission = commandToEntity(command, new Permission());
        permission.setCreatedBy(authenticationService.getCurrentLoggedInUserUsername());
        permission.setCreatedTs(now);
        return modelMapper.map(permissionRepository.save(permission), PermissionDto.class);
    }

    @Transactional
    public PermissionDto update(PermissionCommand command, Long id) {
        logger.debug("Updating permission by id {}", id);
        Instant now = now();
        Permission permission = findPermissionEntityById(id, permissionRepository);
        commandToEntity(command, permission);
        permission.setModifiedBy(authenticationService.getCurrentLoggedInUserUsername());
        permission.setModifiedTs(now);
        return modelMapper.map(permissionRepository.save(permission), PermissionDto.class);
    }

    public PermissionDto findById(Long id) {
        return modelMapper.map(findPermissionEntityById(id, permissionRepository), PermissionDto.class);
    }

    public List<PermissionDto> findAll() {
        return permissionRepository.findAll()
                .stream()
                .map(r -> modelMapper.map(r, PermissionDto.class))
                .collect(Collectors.toList());
    }

    public Page<PermissionDto> search(PermissionSearchCommand command, Pageable pageable) {
        QPermission permission = QPermission.permission1;
        BooleanBuilder builder = new BooleanBuilder();
        QueryBuilderUtil.buildCreatedModifiedAndIdConditions(command, permission._super, permission.id, builder);
        QueryBuilderUtil.equalsEnum(builder, permission.permission, command.getPermission());
        return permissionRepository.findAll(builder.getValue() != null ? builder.getValue() : builder, pageable)
                .map(u -> modelMapper.map(u, PermissionDto.class));
    }

    @Transactional
    public void deleteById(Long id) {
        logger.debug("Deleting permission by id {}", id);
        Permission permission = findPermissionEntityById(id, permissionRepository);
        permissionRepository.delete(permission);
    }

}
