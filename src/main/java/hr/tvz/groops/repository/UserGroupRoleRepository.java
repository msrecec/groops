package hr.tvz.groops.repository;

import hr.tvz.groops.model.*;
import hr.tvz.groops.model.pk.UserGroupRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGroupRoleRepository extends JpaRepository<UserGroupRole, UserGroupRoleId>, QuerydslPredicateExecutor<UserGroupRole> {
    Optional<UserGroupRole> findByUserGroupAndRole(UserGroup userGroup, Role role);
    List<UserGroupRole> findByUserGroup(UserGroup userGroup);
    @Query(value = "SELECT ugr FROM UserGroupRole ugr " +
            "INNER JOIN FETCH ugr.userGroup ug " +
            "INNER JOIN FETCH ug.user ugu " +
            "INNER JOIN FETCH ug.group ugg " +
            "INNER JOIN FETCH ugr.role ugrr " +
            "WHERE ugg = :group ")
    List<UserGroupRole> findByGroup(@Param("group") Group group);

    boolean existsByUserGroupAndRole(UserGroup userGroup, Role role);

    @Query(value = "SELECT COUNT(ugr) FROM UserGroupRole ugr " +
            "INNER JOIN ugr.userGroup ug " +
            "INNER JOIN ug.user u " +
            "WHERE u = :user AND ugr.role = :role")
    Integer countAllByUserAndRole(@Param("user") User user, @Param("role") Role role);

    @Query(value = "SELECT COUNT(ugr) FROM UserGroupRole ugr " +
            "INNER JOIN ugr.userGroup ug " +
            "WHERE ug.group = :group AND ugr.role = :role")
    Integer countAllByGroupAndRole(@Param("group") Group group, @Param("role") Role role);
}
