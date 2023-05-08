package hr.tvz.groops.repository;

import hr.tvz.groops.model.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long>, QuerydslPredicateExecutor<DirectMessage> {
}
