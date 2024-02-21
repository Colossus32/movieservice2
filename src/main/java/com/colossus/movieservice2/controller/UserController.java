package com.colossus.movieservice2.controller;

import com.colossus.movieservice2.entity.UserEditRequest;
import com.colossus.movieservice2.entity.UserRegistrationRequest;
import com.colossus.movieservice2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Endpoint for registering a new user
     *
     * @param userRegistrationRequest the user registration request
     * @return ResponseEntity with the result of the user registration
     */
    @PostMapping
    public ResponseEntity<?> userRegistration(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        return userService.registerUser(userRegistrationRequest);
    }

    /**
     * Retrieve a user by ID.
     *
     * @param userId the ID of the user making the request
     * @param id the ID of the user to retrieve
     * @return ResponseEntity representing the user data
     */
    @GetMapping({"/{id}"})
    public ResponseEntity<?> getUser(
            @RequestHeader("User-Id") long userId,
            @RequestParam("id") long id) {
        return userService.getUserById(userId, id);
    }

    /**
     * Update the user with the given ID.
     *
     * @param userId the ID of the user making the request
     * @param id the ID of the user to be updated
     * @param userEditRequest the request body containing the updated user information
     * @return ResponseEntity representing the result of the update operation
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @RequestHeader("User-Id") long userId, @RequestParam("id") long id,
            @RequestBody UserEditRequest userEditRequest) {
        return userService.updateUser(userId, id, userEditRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @RequestHeader("User-Id") long userId, @RequestParam("id") long id) {
        return userService.deleteUser(userId, id);
    }
}
