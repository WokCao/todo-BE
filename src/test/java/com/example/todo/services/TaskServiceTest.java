package com.example.todo.services;

import com.example.todo.DTOs.CreateTaskDTO;
import com.example.todo.enums.PRIORITY;
import com.example.todo.enums.STATUS;
import com.example.todo.models.TaskModel;
import com.example.todo.models.UserModel;
import com.example.todo.repositories.TaskRepository;
import com.example.todo.repositories.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private Validator validator;

    @InjectMocks
    private TaskService taskService;

    private UserModel mockUser;

    @BeforeEach
    void setUp() {
        // Initialize validator with proper resource management
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        // Mock user
        mockUser = new UserModel();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setFullname("John Doe");
    }

    private void setupSecurityContext() {
        // Setup mock security context - only for tests that need it
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
    }

    @Test
    void createTask_shouldSaveTaskSuccessfully() throws Exception {
        setupSecurityContext();
        // Arrange
        CreateTaskDTO dto = new CreateTaskDTO();
        dto.setTitle("Test Task");
        dto.setDescription("This is a test task");
        dto.setPriority(PRIORITY.high);
        dto.setStatus(STATUS.inProgress);
        dto.setDueDate(LocalDateTime.now().plusDays(7));

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(mockUser));

        TaskModel savedTask = new TaskModel();
        savedTask.setId(1L);
        savedTask.setTitle(dto.getTitle());
        savedTask.setDescription(dto.getDescription());
        savedTask.setPriority(dto.getPriority());
        savedTask.setStatus(dto.getStatus());
        savedTask.setDueDate(dto.getDueDate());
        savedTask.setUser(mockUser);

        when(taskRepository.save(any(TaskModel.class))).thenReturn(savedTask);

        // Act
        TaskModel result = taskService.createTask(dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Task");
        assertThat(result.getUser().getEmail()).isEqualTo("test@example.com");

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(taskRepository, times(1)).save(any(TaskModel.class));

        System.out.println("Task created successfully");
    }

    @Test
    void createTask_shouldValidateDueDate() {
        // Create a DTO with due date < 60 minutes in the future (should fail)
        CreateTaskDTO invalidDto = new CreateTaskDTO();
        invalidDto.setTitle("Test Task");
        invalidDto.setDescription("This should fail");
        invalidDto.setPriority(PRIORITY.high);
        invalidDto.setStatus(STATUS.inProgress);
        invalidDto.setDueDate(LocalDateTime.now().plusMinutes(30)); // Only 30 minutes in future

        // Validate the DTO
        Set<ConstraintViolation<CreateTaskDTO>> violations = validator.validate(invalidDto);
        assertThat(violations).isNotEmpty(); // Should have validation errors

        // Test with valid due date (> 60 minutes)
        CreateTaskDTO validDto = new CreateTaskDTO();
        validDto.setTitle("Test Task");
        validDto.setDescription("This should pass");
        validDto.setPriority(PRIORITY.high);
        validDto.setStatus(STATUS.inProgress);
        validDto.setDueDate(LocalDateTime.now().plusHours(2)); // 2 hours in future

        violations = validator.validate(validDto);
        assertThat(violations).isEmpty(); // Should have no validation errors
    }

    @Test
    void updateTask_shouldUpdateTaskSuccessfully() throws Exception {
        setupSecurityContext();

        // Arrange
        Long taskId = 1L;
        CreateTaskDTO updateDto = new CreateTaskDTO();
        updateDto.setTitle("Updated Task");
        updateDto.setDescription("Updated description");
        updateDto.setPriority(PRIORITY.medium);
        updateDto.setStatus(STATUS.completed);
        updateDto.setDueDate(LocalDateTime.now().plusDays(5));

        // Mock the authorization check
        TaskModel existingTask = new TaskModel();
        existingTask.setId(taskId);
        existingTask.setTitle("Original Task");
        existingTask.setDescription("Original description");
        existingTask.setPriority(PRIORITY.high);
        existingTask.setStatus(STATUS.inProgress);
        existingTask.setDueDate(LocalDateTime.now().plusDays(3));
        existingTask.setUser(mockUser);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(mockUser));
        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(existingTask));

        // Mock the save operation
        TaskModel updatedTask = new TaskModel();
        updatedTask.setId(taskId);
        updatedTask.setTitle(updateDto.getTitle());
        updatedTask.setDescription(updateDto.getDescription());
        updatedTask.setPriority(updateDto.getPriority());
        updatedTask.setStatus(updateDto.getStatus());
        updatedTask.setDueDate(updateDto.getDueDate());
        updatedTask.setUser(mockUser);

        when(taskRepository.save(any(TaskModel.class))).thenReturn(updatedTask);

        // Act
        TaskModel result = taskService.updateTask(taskId, updateDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(taskId);
        assertThat(result.getTitle()).isEqualTo("Updated Task");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getPriority()).isEqualTo(PRIORITY.medium);
        assertThat(result.getStatus()).isEqualTo(STATUS.completed);
        assertThat(result.getUser().getEmail()).isEqualTo("test@example.com");

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(TaskModel.class));
    }

    @Test
    void updateTask_shouldThrowExceptionWhenTaskNotFound() throws Exception {
        setupSecurityContext();

        // Arrange
        Long taskId = 1L;
        CreateTaskDTO updateDto = new CreateTaskDTO();
        updateDto.setTitle("Updated Task");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(mockUser));
        when(taskRepository.findById(taskId))
                .thenReturn(Optional.empty()); // Task not found

        // Act & Assert
        assertThatThrownBy(() -> taskService.updateTask(taskId, updateDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Task not found");

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any(TaskModel.class));
    }

    @Test
    void updateTask_shouldThrowExceptionWhenUserIsNotAuthorized() throws Exception {
        setupSecurityContext();

        // Arrange
        Long taskId = 1L;
        CreateTaskDTO updateDto = new CreateTaskDTO();
        updateDto.setTitle("Updated Task");

        // Create a different user who doesn't own the task
        UserModel differentUser = new UserModel();
        differentUser.setId(2L);
        differentUser.setEmail("different@example.com");

        TaskModel existingTask = new TaskModel();
        existingTask.setId(taskId);
        existingTask.setTitle("Original Task");
        existingTask.setUser(differentUser); // Task belongs to different user

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(mockUser)); // Current user is mockUser (id=1)
        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(existingTask)); // Task belongs to user with id=2

        // Act & Assert
        assertThatThrownBy(() -> taskService.updateTask(taskId, updateDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not authorized to update this task");

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any(TaskModel.class));
    }

    @Test
    void deleteTask_shouldDeleteTaskSuccessfully() throws Exception {
        setupSecurityContext();

        // Arrange
        Long taskId = 1L;

        TaskModel existingTask = new TaskModel();
        existingTask.setId(taskId);
        existingTask.setTitle("Task to delete");
        existingTask.setUser(mockUser);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(mockUser));
        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(existingTask));

        // Act
        taskService.deleteTask(taskId);

        // Assert
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).delete(existingTask);
    }

    @Test
    void deleteTask_shouldThrowExceptionWhenTaskNotFound() throws Exception {
        setupSecurityContext();

        // Arrange
        Long taskId = 1L;

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(mockUser));
        when(taskRepository.findById(taskId))
                .thenReturn(Optional.empty()); // Task not found

        // Act & Assert
        assertThatThrownBy(() -> taskService.deleteTask(taskId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Task not found");

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).delete(any(TaskModel.class));
    }

    @Test
    void deleteTask_shouldThrowExceptionWhenUserIsNotAuthorized() throws Exception {
        setupSecurityContext();

        // Arrange
        Long taskId = 1L;

        // Create a different user who doesn't own the task
        UserModel differentUser = new UserModel();
        differentUser.setId(2L);
        differentUser.setEmail("different@example.com");

        TaskModel existingTask = new TaskModel();
        existingTask.setId(taskId);
        existingTask.setTitle("Task to delete");
        existingTask.setUser(differentUser); // Task belongs to different user

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(mockUser)); // Current user is mockUser (id=1)
        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(existingTask)); // Task belongs to user with id=2

        // Act & Assert
        assertThatThrownBy(() -> taskService.deleteTask(taskId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not authorized to update this task");

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).delete(any(TaskModel.class));
    }
}

