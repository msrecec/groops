package hr.tvz.groops.repository;

import hr.tvz.groops.model.GroupMessageLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMessageLikeRepository extends JpaRepository<GroupMessageLike, Long>, QuerydslPredicateExecutor<GroupMessageLike> {
}
