package com.example.todo.services;

import com.example.todo.models.TaskModel;
import com.example.todo.models.UserModel;
import com.example.todo.repositories.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReminderServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MailService mailService;

    @Autowired
    @InjectMocks
    private ReminderService reminderService;

    @Test
    void shouldSendEmailForTasksDueIn24Hours() {
        // Arrange
        UserModel user = new UserModel();
        user.setEmail("chquoc21@clc.fitus.edu.vn");
        user.setFullname("John Doe");

        TaskModel task = new TaskModel();
        task.setTitle("Finish report");
        task.setDueDate(LocalDateTime.now().plusHours(5));
        task.setUser(user);

        when(taskRepository.findByDueDateBetween(any(), any()))
                .thenReturn(List.of(task));

        // Act
        reminderService.sendReminder();

        // Assert
        verify(mailService, times(1)).sendTaskDueDateReminder(
                eq("chquoc21@clc.fitus.edu.vn"),
                eq(task)
        );
    }

}
