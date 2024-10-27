package com.expleo.rakbank.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("Test.User@example.com");
        user.setPassword("password123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "User should be valid");
    }

    @Test
    public void testInvalidName() {
        User user = new User();
        user.setName(""); // Invalid name
        user.setEmail("Test.User@example.com");
        user.setPassword("password123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "User should be invalid due to empty name");
        assertEquals("Name is mandatory", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidEmail() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("invalid-email"); // Invalid email
        user.setPassword("password123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "User should be invalid due to invalid email");
        assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidPassword() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("Test.User@example.com");
        user.setPassword("short"); // Invalid password

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "User should be invalid due to short password");
        assertEquals("Password must be at least 8 characters long", violations.iterator().next().getMessage());
    }
}
