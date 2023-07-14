package hr.tvz.groops.repository;

import hr.tvz.groops.model.GroupRequest;
import hr.tvz.groops.model.pk.GroupRequestId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRequestRepository extends JpaRepository<GroupRequest, GroupRequestId>, QuerydslPredicateExecutor<GroupRequest> {
}
