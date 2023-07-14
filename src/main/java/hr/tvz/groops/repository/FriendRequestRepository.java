package hr.tvz.groops.repository;

import hr.tvz.groops.model.FriendRequest;
import hr.tvz.groops.model.pk.FriendRequestId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, FriendRequestId>, QuerydslPredicateExecutor<FriendRequest> {
}
