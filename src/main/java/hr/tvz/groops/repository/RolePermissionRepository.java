package hr.tvz.groops.repository;

import hr.tvz.groops.model.RolePermission;
import hr.tvz.groops.model.pk.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId>, QuerydslPredicateExecutor<RolePermission> {
}
