package com.example.todo.repositories;

import com.example.todo.models.TaskModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, Long> {
    @Query("SELECT t FROM TaskModel t WHERE t.user.email = :email")
    Page<TaskModel> findTasksByUserEmail(String email, Pageable pageable);
}
