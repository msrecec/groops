package hr.tvz.groops.repository;

import hr.tvz.groops.model.Group;
import hr.tvz.groops.model.Role;
import hr.tvz.groops.model.UserGroup;
import hr.tvz.groops.model.UserGroupRole;
import hr.tvz.groops.model.pk.UserGroupRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGroupRoleRepository extends JpaRepository<UserGroupRole, UserGroupRoleId>, QuerydslPredicateExecutor<UserGroupRole> {
    Optional<UserGroupRole> findByUserGroupAndRole(UserGroup userGroup, Role role);
    List<UserGroupRole> findByUserGroup(UserGroup userGroup);

    boolean existsByUserGroupAndRole(UserGroup userGroup, Role role);

    Integer countAllByGroupAndRole(Group group, Role role);
}
