package hr.tvz.groops.repository;

import hr.tvz.groops.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long>, QuerydslPredicateExecutor<PostLike> {
}
