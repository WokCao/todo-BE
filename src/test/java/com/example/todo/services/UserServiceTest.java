package com.example.todo.services;

import com.example.todo.DTOs.UpdateUserDTO;
import com.example.todo.models.UserModel;
import com.example.todo.repositories.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserModel existingUser;
    private Validator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        existingUser = new UserModel();
        existingUser.setId(1L);
        existingUser.setFullname("Old Name");
        existingUser.setPassword("oldPass");

        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void shouldUpdateFullNameOnlySuccessfully() {
        UserService spyService = spy(userService);

        // given
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setFullname("New Fullname");

        doReturn(Optional.of(existingUser)).when(spyService).getCurrentUser();
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        UserModel updatedUser = spyService.updateUser(dto);

        // then
        assertEquals("New Fullname", updatedUser.getFullname());
        assertEquals("oldPass", updatedUser.getPassword()); // password unchanged
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    public void shouldThrowExceptionWhenFullNameIsInvalid() {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setFullname("Invalid Fullname 1");

        Set<ConstraintViolation<UpdateUserDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        violations.forEach(v -> System.out.println(v.getMessage()));
    }

    @Test
    public void shouldUpdatePasswordOnlySuccessfully() {
        UserService spyService = spy(userService);

        // given
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setPassword("newPassword");

        doReturn(Optional.of(existingUser)).when(spyService).getCurrentUser();
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        UserModel updatedUser = spyService.updateUser(dto);

        // then
        assertEquals("Old Name", updatedUser.getFullname()); // fullname unchanged
        assertEquals("encodedPassword", updatedUser.getPassword());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    public void shouldUpdateFullNameAndPasswordSuccessfully() {
        UserService spyService = spy(userService);

        // given
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setFullname("Updated Name");
        dto.setPassword("newPassword");

        doReturn(Optional.of(existingUser)).when(spyService).getCurrentUser();
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        UserModel updatedUser = spyService.updateUser(dto);

        // then
        assertEquals("Updated Name", updatedUser.getFullname());
        assertEquals("encodedPassword", updatedUser.getPassword());
        verify(userRepository, times(1)).save(existingUser);
    }
}
