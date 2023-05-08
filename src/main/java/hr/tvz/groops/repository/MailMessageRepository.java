package hr.tvz.groops.repository;

import hr.tvz.groops.model.MailMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MailMessageRepository extends JpaRepository<MailMessage, Long>, QuerydslPredicateExecutor<MailMessage> {
    @Modifying
    @Query(value = "DELETE FROM MailMessage mm WHERE mm.id NOT IN (SELECT m.mailMessage.id FROM Mail m WHERE m.mailMessage.id = mm.id) ")
    void deleteAllUnlinked();
}
