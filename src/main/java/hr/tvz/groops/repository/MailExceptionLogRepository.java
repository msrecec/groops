package hr.tvz.groops.repository;

import hr.tvz.groops.model.MailExceptionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MailExceptionLogRepository extends JpaRepository<MailExceptionLog, Long>, QuerydslPredicateExecutor<MailExceptionLog> {
}
