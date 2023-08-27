package hr.tvz.groops.repository;

import hr.tvz.groops.model.Comment;
import hr.tvz.groops.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {
    Integer countAllByPost(Post post);
    List<Comment> findAllByPost(Post post);
}
