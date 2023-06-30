package hr.tvz.groops.repository;

import hr.tvz.groops.model.UserGroupRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupRoleRepository extends JpaRepository<UserGroupRole, Long>, QuerydslPredicateExecutor<UserGroupRole> {
}
