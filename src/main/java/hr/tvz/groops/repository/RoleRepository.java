package hr.tvz.groops.repository;

import hr.tvz.groops.model.Role;
import hr.tvz.groops.model.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, QuerydslPredicateExecutor<Role> {
    Optional<Role> findRoleByRole(RoleEnum role);
}
