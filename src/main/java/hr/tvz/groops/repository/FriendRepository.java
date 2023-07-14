package hr.tvz.groops.repository;

import hr.tvz.groops.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>, QuerydslPredicateExecutor<Friend> {
}
