package com.example.todo.services;

import com.example.todo.DTOs.CreateTaskDTO;
import com.example.todo.models.TaskModel;
import com.example.todo.models.UserModel;
import com.example.todo.repositories.TaskRepository;
import com.example.todo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public TaskModel createTask(CreateTaskDTO createTaskDTO) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Find the user
        UserModel userModel = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TaskModel taskModel = new TaskModel();
        taskModel.setTitle(createTaskDTO.getTitle());
        taskModel.setDescription(createTaskDTO.getDescription());
        taskModel.setPriority(createTaskDTO.getPriority());
        taskModel.setStatus(createTaskDTO.getStatus());
        taskModel.setDueDate(createTaskDTO.getDueDate());

        taskModel.setUser(userModel);

        return taskRepository.save(taskModel);
    }

    private TaskModel checkUserAuthorization(Long id) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Find the user
        UserModel userModel = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TaskModel taskModel = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!taskModel.getUser().getId().equals(userModel.getId())) {
            throw new RuntimeException("User not authorized to update this task");
        }

        return taskModel;
    }

    @Transactional
    public TaskModel updateTask(Long id, CreateTaskDTO createTaskDTO) throws Exception {
        TaskModel taskModel = checkUserAuthorization(id);

        taskModel.setTitle(createTaskDTO.getTitle());
        taskModel.setDescription(createTaskDTO.getDescription());
        taskModel.setPriority(createTaskDTO.getPriority());
        taskModel.setStatus(createTaskDTO.getStatus());
        taskModel.setDueDate(createTaskDTO.getDueDate());
        return taskRepository.save(taskModel);
    }

    public void deleteTask(Long id) throws Exception {
        TaskModel taskModel = checkUserAuthorization(id);

        taskRepository.delete(taskModel);
    }

    public TaskModel getTask(Long id) throws Exception {
        return checkUserAuthorization(id);
    }
}
