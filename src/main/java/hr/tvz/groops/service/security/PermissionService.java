package hr.tvz.groops.service.security;

import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.criteria.Searchable;
import hr.tvz.groops.model.Permission;
import hr.tvz.groops.model.enums.PermissionEnum;
import hr.tvz.groops.repository.PermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.function.Supplier;

@Service
public class PermissionService implements Searchable {
    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);
    private final PermissionRepository permissionRepository;
    private final AuthenticationService authenticationService;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository, AuthenticationService authenticationService) {
        this.permissionRepository = permissionRepository;
        this.authenticationService = authenticationService;
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT, propagation = Propagation.MANDATORY)
    public Permission getOrCreatePermission(PermissionEnum permission, Instant now) {
        return permissionRepository.findPermissionByPermission(permission).orElseGet(getPermissionSupplier(permission, now));
    }

    private Supplier<Permission> getPermissionSupplier(PermissionEnum permissionEnum, Instant now) {
        return () -> {
            logger.info("Creating permission: {}", permissionEnum.name());
            Permission permission = Permission.builder()
                    .permission(permissionEnum)
                    .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                    .createdTs(now)
                    .build();
            return permissionRepository.saveAndFlush(permission);
        };
    }

}
