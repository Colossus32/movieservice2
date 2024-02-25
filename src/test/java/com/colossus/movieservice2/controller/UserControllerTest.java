package com.colossus.movieservice2.controller;

import com.colossus.movieservice2.entity.User;
import com.colossus.movieservice2.entity.UserRegistrationRequest;
import com.colossus.movieservice2.entity.UserUpdateRequest;
import com.colossus.movieservice2.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void userRegistrationSuccessful() {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest("test@test.com", "testUsername", "test");

        // When
        when(userService.registerUser(any(UserRegistrationRequest.class))).thenReturn(true);
        ResponseEntity<?> responseEntity = userController.userRegistration(request);

        // Then
        verify(userService, times(1)).registerUser(request); // Verify that userService.registerUser was called once with the given request
        // Assert that the response entity is a successful response
        // You may want to add more assertions depending on your application logic
        assert responseEntity.getStatusCode().is2xxSuccessful();
    }

    @Test
    void userRegistrationFailed() {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest("test@test.com", "testUsername", "test");

        // When
        when(userService.registerUser(any(UserRegistrationRequest.class))).thenReturn(false);
        ResponseEntity<?> responseEntity = userController.userRegistration(request);

        // Then
        verify(userService, times(1)).registerUser(request); // Verify that userService.registerUser was called once with the given request
        // Assert that the response entity is an internal server error
        // You may want to add more assertions depending on your application logic
        assert responseEntity.getStatusCode().is5xxServerError();
    }



    @Test
    void getUser_Unauthorized() {
        // Given
        long userId = 123;
        long requestedUserId = 456;

        // When
        ResponseEntity<?> responseEntity = userController.getUser(requestedUserId, String.valueOf(userId));

        // Then
        // Verify that userService.findById is not called since the user is unauthorized
        verify(userService, never()).findById(anyLong());
        assert responseEntity.getStatusCode().is4xxClientError(); // Assert that the response is forbidden (403)
    }

    @Test
    void getUser_notFound() { // Test for user not found
        // Given
        long userId = 456;
        long requestedUserId = 456;

        // When
        when(userService.findById(requestedUserId)).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = userController.getUser(requestedUserId, String.valueOf(userId));

        // Then
        verify(userService, times(1)).findById(requestedUserId);
        assert responseEntity.getStatusCode().is4xxClientError();
    }

    @Test
    void getUserSuccessful() {
        // Given
        long userId = 123;
        long requestedUserId = 123;

        // When
        when(userService.findById(requestedUserId)).thenReturn(Optional.of(new User()));
        ResponseEntity<?> responseEntity = userController.getUser(requestedUserId, String.valueOf(userId));

        // Then
        verify(userService, times(1)).findById(requestedUserId);
        assert responseEntity.getStatusCode().is2xxSuccessful();
    }

    @Test
    void updateUserSuccessful() {
        // Given
        long userId = 123;
        long requestedUserId = 123;
        UserUpdateRequest request = new UserUpdateRequest("testUsername", "test");

        // When
        when(userService.updateUser(requestedUserId, request)).thenReturn(true);
        ResponseEntity<?> responseEntity = userController.updateUser(requestedUserId, userId, request);

        // Then
        verify(userService, times(1)).updateUser(requestedUserId, request);
        assert responseEntity.getStatusCode().is2xxSuccessful();
    }

    @Test
    void updateUserFailed_notAuthorized() {
        // Given
        long userId = 123;
        long requestedUserId = 456;

        // When
        ResponseEntity<?> responseEntity = userController.updateUser(requestedUserId, userId, new UserUpdateRequest("testUsername", "test"));

        // Then
        verify(userService, never()).updateUser(anyLong(), any());
        assert responseEntity.getStatusCode().is4xxClientError();
    }

    @Test
    void updateUserFailed_userNotFound() {
        // Given
        long userId = 123;
        long requestedUserId = 123;
        UserUpdateRequest request = new UserUpdateRequest("testUsername", "test");

        // When
        when(userService.updateUser(requestedUserId, request)).thenReturn(false);
        ResponseEntity<?> responseEntity = userController.updateUser(requestedUserId, userId, request);

        // Then
        verify(userService, times(1)).updateUser(requestedUserId, request);
        assert responseEntity.getStatusCode().is5xxServerError();
    }

    @Test
    void deleteUserSuccessful() {
        // Given
        long userId = 123;
        long requestedUserId = 123;

        // When
        when(userService.deleteUser(requestedUserId)).thenReturn(true);
        ResponseEntity<?> responseEntity = userController.deleteUser(requestedUserId, userId);

        // Then
        verify(userService, times(1)).deleteUser(requestedUserId);
        assert responseEntity.getStatusCode().is2xxSuccessful();
    }

    @Test
    void deleteUserFailed_notAuthorized() {
        // Given
        long userId = 123;
        long requestedUserId = 456;

        // When
        ResponseEntity<?> responseEntity = userController.deleteUser(requestedUserId, userId);

        // Then
        verify(userService, never()).deleteUser(anyLong());
        assert responseEntity.getStatusCode().is4xxClientError();
    }

    @Test
    void deleteUserFailed_userNotFound() {
        // Given
        long userId = 123;
        long requestedUserId = 123;

        // When
        when(userService.deleteUser(requestedUserId)).thenReturn(false);
        ResponseEntity<?> responseEntity = userController.deleteUser(requestedUserId, userId);

        // Then
        verify(userService, times(1)).deleteUser(requestedUserId);
        assert responseEntity.getStatusCode().is5xxServerError();
    }
}