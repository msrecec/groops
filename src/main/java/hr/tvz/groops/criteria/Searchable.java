package hr.tvz.groops.criteria;

import hr.tvz.groops.model.Permission;
import hr.tvz.groops.model.Role;
import hr.tvz.groops.model.User;
import hr.tvz.groops.repository.PermissionRepository;
import hr.tvz.groops.repository.RoleRepository;
import hr.tvz.groops.repository.UserRepository;
import org.jetbrains.annotations.NotNull;

import javax.persistence.EntityNotFoundException;

import static hr.tvz.groops.constants.CrudExceptionEnum.*;

public interface Searchable {
    default @NotNull Role findRoleEntityById(@NotNull Long id, @NotNull RoleRepository roleRepository) {
        return roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ROLE_NOT_FOUND_BY_ID.getMessageComposed(id)));
    }

    default @NotNull Permission findPermissionEntityById(@NotNull Long id, @NotNull PermissionRepository permissionRepository) {
        return permissionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(PERMISSION_NOT_FOUND_BY_ID.getMessageComposed(id)));
    }

    default @NotNull User findUserEntityById(@NotNull Long id, @NotNull UserRepository userRepository) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_BY_ID.getMessageComposed(id)));
    }

    default @NotNull User findUserEntityByUsername(@NotNull String username, @NotNull UserRepository userRepository) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_BY_USERNAME.getMessageComposed(username)));
    }
}
