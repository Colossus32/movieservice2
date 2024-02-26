package com.colossus.movieservice2.service;

import com.colossus.movieservice2.entity.User;
import com.colossus.movieservice2.entity.UserRegistrationRequest;
import com.colossus.movieservice2.entity.UserUpdateRequest;
import com.colossus.movieservice2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testRegisterUser_SuccessfulRegistration() {
        // Prepare test data
        UserRegistrationRequest request = new UserRegistrationRequest("test@example.com", "testuser", "Test User");

        // Mock repository behavior
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // Call the method under test
        boolean result = userService.registerUser(request);

        // Assert that registration is successful
        assertTrue(result);

        // Verify that userRepository.save() is called with the expected argument
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals(request.email()) &&
                        user.getUsername().equals(request.username()) &&
                        user.getName().equals(request.name())
        ));
    }

    @Test
    void testRegisterUser_InvalidRegistrationRequest() {
        // Prepare invalid test data (e.g., missing required fields)
        UserRegistrationRequest request = new UserRegistrationRequest(null, null, null);

        // Call the method under test
        boolean result = userService.registerUser(request);

        // Assert that registration fails due to invalid request
        assertFalse(result);

        // Verify that userRepository.save() is not called
        verifyNoInteractions(userRepository);
    }

    @Test
    void testFindById_UserExists() {
        // Prepare test data
        long userId = 1L;
        User user = new User("test@example.com", "testuser", "Test User");

        // Mock repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Call the method under test
        Optional<User> result = userService.findById(userId);

        // Assert that user is found
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void testFindById_UserDoesNotExist() {
        // Prepare test data
        long userId = 1L;

        // Mock repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Call the method under test
        Optional<User> result = userService.findById(userId);

        // Assert that user is not found
        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateUser_UserExistsAndUpdatedSuccessfully() {
        // Prepare test data
        long userId = 1L;
        UserUpdateRequest updateRequest = new UserUpdateRequest("updateduser", "Updated Name");
        User existingUser = new User("test@example.com", "testuser", "Test User");
        User updatedUser = new User("test@example.com", "updateduser", "Updated Name");

        // Mock repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername(updateRequest.username())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Call the method under test
        boolean result = userService.updateUser(userId, updateRequest);

        // Assert that user is updated successfully
        assertTrue(result);
        assertEquals(updateRequest.username(), updatedUser.getUsername());
        assertEquals(updateRequest.name(), updatedUser.getName());
    }

    @Test
    void testUpdateUser_UserDoesNotExist() {
        // Prepare test data
        long userId = 1L;
        UserUpdateRequest updateRequest = new UserUpdateRequest("updateduser", "Updated Name");

        // Mock repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Call the method under test
        boolean result = userService.updateUser(userId, updateRequest);

        // Assert that user update fails because user does not exist
        assertFalse(result);
    }

    @Test
    void testUpdateUser_UsernameAlreadyExists() {
        // Prepare test data
        long userId = 1L;
        UserUpdateRequest updateRequest = new UserUpdateRequest("existinguser", "Updated Name");
        User existingUser = new User("test@example.com", "existinguser", "Existing User");

        // Mock repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername(updateRequest.username())).thenReturn(Optional.of(existingUser));

        // Call the method under test
        boolean result = userService.updateUser(userId, updateRequest);

        // Assert that user update fails because username already exists
        assertFalse(result);
    }

    @Test
    void testDeleteUser_UserExists() {
        // Prepare test data
        long userId = 1L;
        User existingUser = new User("test@example.com", "testuser", "Test User");

        // Mock repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Call the method under test
        boolean result = userService.deleteUser(userId);

        // Assert that user is deleted successfully
        assertTrue(result);
        verify(userRepository).delete(existingUser);
    }

    @Test
    void testDeleteUser_UserDoesNotExist() {
        // Prepare test data
        long userId = 1L;

        // Mock repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Call the method under test
        boolean result = userService.deleteUser(userId);

        // Assert that user deletion fails because user does not exist
        assertFalse(result);

        // Verify that userRepository.findById() is called with the expected argument
        verify(userRepository).findById(userId);
        // Verify that userRepository.delete() is not called
        verifyNoMoreInteractions(userRepository);
    }
}