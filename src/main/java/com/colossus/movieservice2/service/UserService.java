package com.colossus.movieservice2.service;

import com.colossus.movieservice2.entity.User;
import com.colossus.movieservice2.entity.UserEditRequest;
import com.colossus.movieservice2.entity.UserRegistrationRequest;
import com.colossus.movieservice2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final String INTERNAL_SERVER_ERROR_MESSAGE = "{\"error\": \"INTERNAL_ERROR\"}";


    /**
     * Check if the user registration request is valid.
     *
     * @param userRegistrationRequest the user registration request to be checked
     * @return true if the user registration request is valid, false otherwise
     */
    private boolean isValidUserRegistrationRequest(
            UserRegistrationRequest userRegistrationRequest) {

        // Log the user registration request for debugging purposes
        log.info("Checking user registration request {}", userRegistrationRequest);

        // Return false if the user registration request is null
        if (userRegistrationRequest == null) {
            return false; // user registration request is null
        }

        // Check if the email and username are not null, the username contains only letters, and the email and username are not already registered
        return userRegistrationRequest.email() != null && // email is not null
                userRegistrationRequest.username() != null && // username is not null
                userRegistrationRequest.username().matches("[a-zA-Z]+") && // username contains only letters
                userRepository.findByEmail(userRegistrationRequest.email()).isEmpty() && // email is not already registered
                userRepository.findByUsername(userRegistrationRequest.username()).isEmpty(); // username is not already registered
    }


    /**
     * Register a new user based on the provided registration request.
     *
     * @param userRegistrationRequest the user registration request
     * @return ResponseEntity with a success message or a bad request message
     */
    public ResponseEntity<?> registerUser(
            UserRegistrationRequest userRegistrationRequest) {

        // Log the user registration request for debugging purposes
        log.info("User registration request: {}", userRegistrationRequest);

        // Validate the user registration request
        if (!isValidUserRegistrationRequest(userRegistrationRequest)) {

            // Log invalid user registration request
            log.warn("Invalid user registration request");

            // Return a bad request response
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(INTERNAL_SERVER_ERROR_MESSAGE);
        }

        // Create a new user and populate the fields
        User user = new User();
        user.setName(userRegistrationRequest.name());
        user.setUsername(userRegistrationRequest.username());
        user.setEmail(userRegistrationRequest.email());

        // Save the user in the database
        userRepository.save(user);

        // Log successful user registration
        log.info("User registered successfully");

        // Return a success response
        return ResponseEntity.ok("User registered successfully");
    }

    /**
     * Get user by id
     * @param userId the id of the user making the request
     * @param id the id of the user to retrieve
     * @return ResponseEntity containing the user information or an error message
     */
    public ResponseEntity<?> getUserById(long userId, long id) {

        // log the user and requested id for debugging
        log.debug("User ID: " + userId + ", Requested ID: " + id);

        // check if the user is authorized to access the requested user information
        if(!isAuthorised(userId, id)) {

            // log unauthorized access for debugging
            log.debug("Unauthorized access for User ID: " + userId + ", Requested ID: " + id);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(INTERNAL_SERVER_ERROR_MESSAGE);
        }

        // log successful retrieval for debugging
        log.debug("Successfully retrieved user information for ID: " + id);

        // return the user information if authorized
        return ResponseEntity.ok(userRepository.findById(id));
    }

    /**
     * Check if the user is authorized based on the user ID and the ID being accessed.
     * @param userId The ID of the user making the request
     * @param id The ID being accessed
     * @return true if the user is authorized, false otherwise
     */
    private boolean isAuthorised(long userId, long id) {
        log.debug("Checking authorization for user ID: {} and accessed ID: {}", userId, id);
        boolean result = userId == id;
        log.debug("User authorization result: {}", result);
        return result;
    }

    /**
     * Updates a user with the specified ID.
     *
     * @param userId the ID of the user making the request
     * @param id the ID of the user to be updated
     * @param userEditRequest the request containing the updated user information
     * @return a ResponseEntity with a success message if the user is updated successfully, or an error message if the update fails
     */
    public ResponseEntity<?> updateUser(long userId, long id, UserEditRequest userEditRequest) {
        log.debug("Updating user with id: {}", id);

        // Check if the user has authorization to update the specified user
        if (!isAuthorised(userId, id)) {
            log.error("User update failed due to lack of authorization");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(INTERNAL_SERVER_ERROR_MESSAGE);
        }

        // Retrieve the user from the database
        Optional<User> fromDatabase = userRepository.findById(id);

        // If the user is not found in the database, return an error message
        if (fromDatabase.isEmpty()) {
            log.error("User with id {} not found in the database", id);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(INTERNAL_SERVER_ERROR_MESSAGE);
        } else {

            // Update the user information and save it to the database
            User forSave = fromDatabase.get();
            if(!isValidUserEditUsernameRequest(userEditRequest.getUsername(), id)) {
                log.error("User update failed due to invalid username");
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(INTERNAL_SERVER_ERROR_MESSAGE);
            }
            forSave.setUsername(userEditRequest.getUsername());
            forSave.setName(userEditRequest.getName());
            userRepository.save(forSave);
            log.debug("User with id {} updated successfully", id);
            return ResponseEntity.ok("User updated successfully");
        }
    }

    /**
     * Check if the user edit username request is valid
     *
     * @param username The new username
     * @param id The user ID
     * @return true if the request is valid, false otherwise
     */
    private boolean isValidUserEditUsernameRequest(String username, long id) {
        Optional<User> fromDatabase = userRepository.findByUsername(username);
        return fromDatabase.map(user -> user.getId() != id).orElse(true);
    }

    /**
     * Deletes a user with the specified id.
     *
     * @param userId the id of the user performing the deletion
     * @param id the id of the user to be deleted
     * @return ResponseEntity with the result of the user deletion
     */
    public ResponseEntity<?> deleteUser(long userId, long id) {
        log.debug("Deleting user with id: {}", id);

        // Check if the user has authorization to delete the specified user
        if (!isAuthorised(userId, id)) {
            log.error("User deletion failed due to lack of authorization");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(INTERNAL_SERVER_ERROR_MESSAGE);
        }

        // Delete the user from the database
        userRepository.deleteById(id);
        log.debug("User with id {} deleted successfully", id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
