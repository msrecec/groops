package hr.tvz.groops.repository;

import hr.tvz.groops.model.PendingVerification;
import hr.tvz.groops.model.User;
import hr.tvz.groops.model.enums.VerificationTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PendingVerificationRepository extends JpaRepository<PendingVerification, Long>, QuerydslPredicateExecutor<PendingVerification> {
    Optional<PendingVerification> findByUserAndVerificationType(User user, VerificationTypeEnum verificationType);
}
