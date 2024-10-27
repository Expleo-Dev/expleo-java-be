package com.expleo.rakbank.controller;

import com.expleo.rakbank.model.User;
import com.expleo.rakbank.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("Test.User@example.com");
        user.setPassword("password123");

        when(userService.createUser(any(User.class))).thenReturn(user);

        ResponseEntity<User> response = userController.createUser(user);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).createUser(user);
    }

    @Test
    public void testListAllUsers() {
        User user1 = new User();
        user1.setName("Test User");
        user1.setEmail("Test.User@example.com");
        User user2 = new User();
        user2.setName("Jane User");
        user2.setEmail("jane.User@example.com");

        when(userService.listAllUsers()).thenReturn(Arrays.asList(user1, user2));

        ResponseEntity<List<User>> response = userController.listAllUsers();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).listAllUsers();
    }

    @Test
    public void testGetUserById() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("Test.User@example.com");

        when(userService.getUserById(1L)).thenReturn(user);

        ResponseEntity<User> response = userController.getUserById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    public void testGetUserByIdNotFound() {
        when(userService.getUserById(1L)).thenReturn(null);

        ResponseEntity<User> response = userController.getUserById(1L);
        assertEquals(404, response.getStatusCodeValue());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    public void testUpdateUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Test User");
        existingUser.setEmail("Test.User@example.com");

        User updatedUser = new User();
        updatedUser.setName("Test Updated");
        updatedUser.setEmail("Test.updated@example.com");

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        ResponseEntity<User> response = userController.updateUser(1L, updatedUser);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedUser, response.getBody());
        verify(userService, times(1)).updateUser(1L, updatedUser);
    }

    @Test
    public void testUpdateUserNotFound() {
        User updatedUser = new User();
        updatedUser.setName("Test Updated");

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(null);

        ResponseEntity<User> response = userController.updateUser(1L, updatedUser);
        assertEquals(404, response.getStatusCodeValue());
        verify(userService, times(1)).updateUser(1L, updatedUser);
    }

    @Test
    public void testDeleteUser() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);
        assertEquals(204, response.getStatusCodeValue());
        verify(userService, times(1)).deleteUser(1L);
    }
}
