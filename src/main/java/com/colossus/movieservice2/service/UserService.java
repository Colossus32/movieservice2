package com.colossus.movieservice2.service;

import com.colossus.movieservice2.entity.User;
import com.colossus.movieservice2.entity.UserRegistrationRequest;
import com.colossus.movieservice2.entity.UserUpdateRequest;
import com.colossus.movieservice2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Registers a new user based on the provided registration request.
     *
     * @param userRegistrationRequest the user registration request containing email and username
     * @return true if registration is successful, false otherwise
     */
    public boolean registerUser(UserRegistrationRequest userRegistrationRequest) {
        boolean registrationSuccessful = checkRegistrationRequest(userRegistrationRequest);
        if (!registrationSuccessful) return false;
        userRepository.save(new User(
                userRegistrationRequest.email(),
                userRegistrationRequest.username(),
                userRegistrationRequest.name()
        ));
        return true;
    }

    /**
     * Checks the validity of the user registration request.
     *
     * @param userRegistrationRequest the user registration request to be checked
     * @return true if the request is valid, false otherwise
     */
    private boolean checkRegistrationRequest(UserRegistrationRequest userRegistrationRequest) {
        // Check if email and username are not null, and if the username contains only letters
        return userRegistrationRequest.email() != null &&
                userRegistrationRequest.username() != null &&
                userRegistrationRequest.username().matches("[a-zA-Z]+") &&
                // Check if the email and username are not already registered
                userRepository.findByEmail(userRegistrationRequest.email()).isEmpty() &&
                userRepository.findByUsername(userRegistrationRequest.username()).isEmpty();
    }


    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user
     * @return an optional containing the user, or empty if not found
     */
    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    /**
     * Updates the user with the given ID using the information provided in the update request
     * @param id The ID of the user to be updated
     * @param updateRequest The request containing the updated user information
     * @return true if the user was successfully updated, false otherwise
     */
    public boolean updateUser(long id, UserUpdateRequest updateRequest) {

        // Check if the user with the given ID exists
        Optional<User> byId = userRepository.findById(id);
        if (byId.isEmpty()) {
            return false;
        }

        // Check if the new username is already taken by another user
        Optional<User> findByUsername = userRepository.findByUsername(updateRequest.username());
        if (findByUsername.isPresent() && findByUsername.get().getId() != id) {
            return false;
        }

        // Update the user information and save to the repository
        User forUpdate = byId.get();
        forUpdate.setUsername(updateRequest.username());
        forUpdate.setName(updateRequest.name());
        userRepository.save(forUpdate);

        return true;
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to be deleted
     * @return true if the user was successfully deleted, false if the user was not found
     */
    public boolean deleteUser(long id) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isEmpty()) {
            return false;
        }
        userRepository.delete(byId.get());
        return true;
    }
}
