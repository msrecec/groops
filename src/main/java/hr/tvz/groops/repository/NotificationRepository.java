package hr.tvz.groops.repository;

import hr.tvz.groops.model.Notification;
import hr.tvz.groops.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, QuerydslPredicateExecutor<Notification> {
    @Query(value = "SELECT COUNT(n.id) FROM Notification n WHERE n.user = :user AND n.read = FALSE")
    Integer countAllByUserOfUnread(User user);

    List<Notification> findAllByUserOrderByIdDesc(User user);
}
