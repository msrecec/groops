package hr.tvz.groops.repository;

import hr.tvz.groops.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Optional;

import static hr.tvz.groops.constants.TimeoutConstants.SHORT_TIMEOUT_MS;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long>, QuerydslPredicateExecutor<Group> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.query.timeout", value = SHORT_TIMEOUT_MS)})
    @Query(value = "SELECT g FROM Group g WHERE g.id=:groupId")
    Optional<Group> findByIdLockByPessimisticWrite(@Param("groupId") Long groupId);
}
