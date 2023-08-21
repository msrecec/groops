package hr.tvz.groops.repository;

import hr.tvz.groops.model.Permission;
import hr.tvz.groops.model.enums.PermissionEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, QuerydslPredicateExecutor<Permission> {
    Optional<Permission> findPermissionByPermission(PermissionEnum permission);
}
