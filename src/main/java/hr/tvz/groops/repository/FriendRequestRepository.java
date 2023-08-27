package hr.tvz.groops.repository;

import hr.tvz.groops.model.FriendRequest;
import hr.tvz.groops.model.User;
import hr.tvz.groops.model.pk.FriendRequestId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, FriendRequestId>, QuerydslPredicateExecutor<FriendRequest> {
    Optional<FriendRequest> findBySenderAndRecipient(User sender, User recipient);
    List<FriendRequest> findAllByRecipient(User recipient);
    List<FriendRequest> findAllBySender(User sender);
}
