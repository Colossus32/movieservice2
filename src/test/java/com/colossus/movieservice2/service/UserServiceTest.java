package com.colossus.movieservice2.service;

import com.colossus.movieservice2.entity.User;
import com.colossus.movieservice2.entity.UserRegistrationRequest;
import com.colossus.movieservice2.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Test
    public void testRegisterUser_ValidRequest() {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest("John Doe", "johndoe", "john@example.com");
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.save(any())).thenReturn(new User());

        UserService service = new UserService(userRepository);

        // Act
        ResponseEntity<?> response = service.registerUser(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully", response.getBody());
    }

    @Test
    public void testRegisterUser_InvalidRequest() {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest("", "", "");
        UserRepository userRepository = mock(UserRepository.class);

        UserService service = new UserService(userRepository);

        // Act
        ResponseEntity<?> response = service.registerUser(request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("{\"error\": \"INTERNAL_ERROR\"}", response.getBody());
    }

    @Test
    public void testRegisterUser_NullRequest() {
        // Arrange
        UserRegistrationRequest request = null;
        UserRepository userRepository = mock(UserRepository.class);

        UserService service = new UserService(userRepository);

        //Act
        ResponseEntity<?> response = service.registerUser(request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("{\"error\": \"INTERNAL_ERROR\"}", response.getBody());
    }
}