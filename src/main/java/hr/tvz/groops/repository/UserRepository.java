package hr.tvz.groops.repository;

import hr.tvz.groops.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

import static hr.tvz.groops.constants.TimeoutConstants.SHORT_TIMEOUT_MS;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.query.timeout", value = SHORT_TIMEOUT_MS)})
    @Query(value = "SELECT u FROM User u WHERE u.id=:id")
    Optional<User> findByIdLockByPessimisticWrite(@Param("id") Long id);
    @Query(value = "SELECT u.id FROM User u WHERE u.username = :username")
    Optional<Long> findIdByUsernameLockByPessimisticWrite(@Param("username") String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.query.timeout", value = SHORT_TIMEOUT_MS)})
    @Query(value = "SELECT u FROM User u WHERE u.username=:username")
    Optional<User> findByUsernameLockByPessimisticWrite(String username);

    Optional<User> findByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.query.timeout", value = SHORT_TIMEOUT_MS)})
    @Query(value = "SELECT u FROM User u WHERE u.verified=FALSE")
    List<User> findAllUnverifiedLockByPessimisticWrite();
}
