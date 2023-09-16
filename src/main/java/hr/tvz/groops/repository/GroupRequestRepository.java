package hr.tvz.groops.repository;

import hr.tvz.groops.model.Group;
import hr.tvz.groops.model.GroupRequest;
import hr.tvz.groops.model.User;
import hr.tvz.groops.model.pk.GroupRequestId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRequestRepository extends JpaRepository<GroupRequest, GroupRequestId>, QuerydslPredicateExecutor<GroupRequest> {
    Optional<GroupRequest> findByGroupAndUser(Group group, User user);
    Boolean existsByGroupAndUser(Group group, User user);

    List<GroupRequest> findByGroup(Group group);
}
