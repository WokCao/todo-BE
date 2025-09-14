package com.example.todo.repositories;

import com.example.todo.enums.PRIORITY;
import com.example.todo.enums.STATUS;
import com.example.todo.models.TaskModel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, Long>, JpaSpecificationExecutor<TaskModel> {
    default Page<TaskModel> findTasksByUserEmailAndStatusAndPriorityBetween(String email, STATUS status, PRIORITY priority, LocalDateTime fromDateTime, LocalDateTime toDateTime, Pageable pageable) {
        return findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter by user email
            predicates.add(cb.equal(root.get("user").get("email"), email));

            // Filter by status (if not ALL)
            if (status != null && status != STATUS.ALL) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // Filter by priority (if not ALL)
            if (priority != null && priority != PRIORITY.ALL) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }

            // Filter by date range
            if (fromDateTime != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), fromDateTime));
            }
            if (toDateTime != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), toDateTime));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
}
