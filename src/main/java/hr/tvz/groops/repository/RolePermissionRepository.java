package hr.tvz.groops.repository;

import hr.tvz.groops.model.Role;
import hr.tvz.groops.model.RolePermission;
import hr.tvz.groops.model.pk.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId>, QuerydslPredicateExecutor<RolePermission> {
    @Query("SELECT rp FROM RolePermission rp WHERE rp.role.id = :roleId AND rp.permission.id = :permissionId")
    Optional<RolePermission> findByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    @Query(value = "SELECT rp FROM RolePermission rp WHERE rp.role=:role")
    List<RolePermission> findByRole(Role role);
}
