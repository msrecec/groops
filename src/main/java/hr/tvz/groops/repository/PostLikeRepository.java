package hr.tvz.groops.repository;

import hr.tvz.groops.model.Post;
import hr.tvz.groops.model.PostLike;
import hr.tvz.groops.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long>, QuerydslPredicateExecutor<PostLike> {
    @Query(value = "SELECT COUNT(pl) FROM PostLike pl WHERE pl.post=:post")
    Integer countAllByPost(@Param("post") Post post);

    Boolean existsByPostAndUser(Post post, User user);

    Optional<PostLike> findPostLikeByPostAndUser(Post post, User user);
}
