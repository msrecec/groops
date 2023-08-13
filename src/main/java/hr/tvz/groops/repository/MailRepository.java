package hr.tvz.groops.repository;

import hr.tvz.groops.model.Mail;
import hr.tvz.groops.model.enums.MailStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface MailRepository extends JpaRepository<Mail, Long>, QuerydslPredicateExecutor<Mail> {

    @Query(value = "SELECT m FROM Mail m " +
            "INNER JOIN FETCH m.mailMessage mm " +
            "INNER JOIN FETCH m.sender ms " +
            "INNER JOIN FETCH m.recipient mr " +
            "WHERE m.mailMessage.id = :mailMessageId AND m.recipient.id = :recipientId AND m.sender.id = :senderId AND m.mailStatus = :mailStatus ")
    Optional<Mail> findMailByMailMessageIdAndRecipientIdAndSenderIdAndMailStatus(@Param("mailMessageId") Long mailMessageId,
                                                                                 @Param("recipientId") Long recipientId,
                                                                                 @Param("senderId") Long senderId,
                                                                                 @Param("mailStatus") MailStatusEnum mailStatus);

    @Query(value = "SELECT m.sender.id, m.recipient.id, m.mailMessage.id FROM Mail m WHERE m.mailStatus = :mailStatus ")
    List<Tuple> findIdSenderIdRecipientIdMailMessageIdByMailStatus(@Param("mailStatus") MailStatusEnum mailStatus);

    @Modifying
    @Query(value = "DELETE FROM Mail m WHERE m.expires < :now")
    void deleteAllExpired(@Param("now") Instant now);

}
