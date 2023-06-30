package hr.tvz.groops.repository;

import hr.tvz.groops.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long>, QuerydslPredicateExecutor<Group> {
}
