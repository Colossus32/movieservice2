package com.colossus.movieservice2.repository;

import com.colossus.movieservice2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by email.
     *
     * @param email the email address of the user to find
     * @return an Optional containing the user, if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find a user by username.
     * @param username the username to search for
     * @return an Optional containing the user if found, otherwise an empty Optional
     */
    Optional<User> findByUsername(String username);
}
