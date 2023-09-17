package hr.tvz.groops.repository;

import hr.tvz.groops.model.Group;
import hr.tvz.groops.model.User;
import hr.tvz.groops.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long>, QuerydslPredicateExecutor<UserGroup> {
    Optional<UserGroup> findByUserAndGroup(User user, Group group);
    boolean existsByUserAndGroup(User user, Group group);
    @Query(value = "SELECT ug.user FROM UserGroup ug WHERE ug.group = :group")
    List<User> findUsersByGroup(@Param("group") Group group);
}
