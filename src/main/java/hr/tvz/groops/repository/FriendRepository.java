package hr.tvz.groops.repository;

import hr.tvz.groops.model.Friend;
import hr.tvz.groops.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>, QuerydslPredicateExecutor<Friend> {
    Boolean existsByFirstUserAndSecondUser(User firstUser, User secondUser);
}
