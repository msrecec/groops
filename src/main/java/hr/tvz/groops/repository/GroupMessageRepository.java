package hr.tvz.groops.repository;

import hr.tvz.groops.model.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long>, QuerydslPredicateExecutor<GroupMessage> {
}
