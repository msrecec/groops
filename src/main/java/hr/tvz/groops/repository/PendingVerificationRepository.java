package hr.tvz.groops.repository;

import hr.tvz.groops.model.PendingVerification;
import hr.tvz.groops.model.User;
import hr.tvz.groops.model.enums.VerificationTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PendingVerificationRepository extends JpaRepository<PendingVerification, Long>, QuerydslPredicateExecutor<PendingVerification> {
    Optional<PendingVerification> findByUserAndVerificationType(User user, VerificationTypeEnum verificationType);

    @Query("SELECT pv.id FROM PendingVerification pv WHERE pv.user = :user")
    List<Long> findIdsByUser(@Param("user") User user);

    List<PendingVerification> findByUser(User user);
}
