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
}

