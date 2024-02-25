package com.colossus.movieservice2.controller;

import com.colossus.movieservice2.entity.User;
import com.colossus.movieservice2.entity.UserRegistrationRequest;
import com.colossus.movieservice2.entity.UserUpdateRequest;
import com.colossus.movieservice2.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users/")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;


    /**
     * Handles user registration request
     *
     * @param request the user registration request
     * @return ResponseEntity with the result of the user registration
     */
    @PostMapping
    public ResponseEntity<?> userRegistration(@RequestBody UserRegistrationRequest request) {
        log.info("Received user registration request: {}", request);

        // Register a new user based on the provided request
        if (!userService.registerUser(request)) {
            log.error("Internal server error occurred during user registration");
            return internalError(); // Return an internal server error response
        }
        log.info("User registration successful");
        return ResponseEntity.ok().build(); // Return a successful response
    }

    /**
     * Get user by ID.
     *
     * @param id The ID of the user
     * @param userId The ID of the requesting user
     * @return ResponseEntity with the user information if authorized, 403 status if unauthorized, or 404 status if user not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable long id, @RequestHeader("User-Id") String userId) {
        // Check if the requesting user is authorized to access the user information
        if (!isAuthorized(Long.parseLong(userId), id)) {
            return ResponseEntity.status(403).build();
        }

        // Retrieve the user information by ID
        Optional<User> user = userService.findById(id);
        // If the user is not found, return 404 status
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // Return the user information
        return ResponseEntity.ok(user.get());
    }

    /**
     * Update user information.
     *
     * @param id The ID of the user to update
     * @param userId The ID of the user making the request
     * @param updateRequest The request body containing the updated user information
     * @return ResponseEntity with status 200 if successful, 403 if not authorized, or 500 for internal error
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @RequestHeader("User-Id") long userId,
                                        @RequestBody UserUpdateRequest updateRequest) {

        // Check if user is authorized to update
        if (!isAuthorized(userId, id)) {
            return ResponseEntity.status(403).build();
        }

        // Update user information
        boolean userUpdated = userService.updateUser(id, updateRequest);

        // Return appropriate response
        if (!userUpdated) {
            return internalError();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint to delete a user by id
     *
     * @param id the id of the user to delete
     * @param userId the id of the requesting user
     * @return ResponseEntity with status 403 if not authorized, ResponseEntity with status 500 if user deletion fails, otherwise ResponseEntity with status 200
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id, @RequestHeader("User-Id") long userId) {

        // Check if the requesting user is authorized to delete the specified user
        if (!isAuthorized(userId, id)) {
            return ResponseEntity.status(403).build();
        }

        // Attempt to delete the user
        boolean userDeleted = userService.deleteUser(id);

        // If user deletion fails, return internal server error
        if (!userDeleted) {
            return internalError();
        }

        // If user deletion is successful, return ok status
        return ResponseEntity.ok().build();
    }

    /**
     * Returns a ResponseEntity with a 500 status code and an error message for internal server error.
     *
     * @return ResponseEntity<String> - the response entity with the error message
     */
    private ResponseEntity<String> internalError() {
        String INTERNAL_SERVER_ERROR_MESSAGE = "{\"error\": \"INTERNAL_ERROR\"}";
        return ResponseEntity.status(500).body(INTERNAL_SERVER_ERROR_MESSAGE);
    }

    /**
     * Checks if the user is authorized based on their user ID and the ID being accessed.
     *
     * @param userId the ID of the user
     * @param id the ID being accessed
     * @return true if the user is authorized, false otherwise
     */
    private boolean isAuthorized(long userId, long id) {
        return userId == id;
    }
}
