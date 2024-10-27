package com.expleo.rakbank.service;

import com.expleo.rakbank.model.User;
import com.expleo.rakbank.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
    }

    @Test
    public void testCreateUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals("hashedPassword", createdUser.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testCreateUser_EmailAlreadyExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Email is already in use", thrown.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testListAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.listAllUsers();

        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetUserById_Found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(1L);

        assertNotNull(foundUser);
        assertEquals(user, foundUser);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        User foundUser = userService.getUserById(1L);

        assertNull(foundUser);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateUser_Success() {
        User updatedUserDetails = new User();
        updatedUserDetails.setName("John Updated");
        updatedUserDetails.setEmail("john.updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUser(1L, updatedUserDetails);

        assertNotNull(updatedUser);
        assertEquals("John Updated", updatedUser.getName());
        assertEquals("john.updated@example.com", updatedUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUser_NotFound() {
        User updatedUserDetails = new User();
        updatedUserDetails.setName("John Updated");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        User updatedUser = userService.updateUser(1L, updatedUserDetails);

        assertNull(updatedUser);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testChangePassword_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq("password123"), any())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("hashedNewPassword");

        boolean result = userService.changePassword(1L, "password123", "newPassword");

        assertTrue(result);
        assertEquals("hashedNewPassword", user.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testChangePassword_InvalidCurrentPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq("wrongPassword"), any())).thenReturn(false);

        boolean result = userService.changePassword(1L, "wrongPassword", "newPassword");

        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }
}
