package com.example.todo.services;

import com.example.todo.models.TaskModel;
import com.example.todo.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReminderService {
    @Autowired
    private MailService mailService;
    @Autowired
    private TaskRepository taskRepository;

    @Scheduled(cron = "0 0 7 * * ?")
    public void sendReminder() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next24Hours = now.plusHours(24);

        List<TaskModel> tasks = taskRepository.findByDueDateBetween(now, next24Hours);

        for (TaskModel task : tasks) {
            mailService.sendTaskDueDateReminder(task.getUser().getEmail(), task);
        }
    }
}
