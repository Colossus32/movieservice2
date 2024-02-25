package com.colossus.movieservice2.controller;

import com.colossus.movieservice2.entity.FavoriteMovie;
import com.colossus.movieservice2.entity.Movie;
import com.colossus.movieservice2.service.FavoriteMovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favorites")
@Slf4j
public class FavoriteMovieController {

    private final FavoriteMovieService favoriteMovieService;

    /**
     * Endpoint for adding a movie to user's favorites
     *
     * @param headerId The User-Id from request header
     * @param userId   The ID of the user
     * @param movieId  The ID of the movie to be added as favorite
     * @return ResponseEntity with status 200 if the movie was added successfully,
     * ResponseEntity with status 403 if the user is not authorized,
     * or internal server error if there was an error
     */
    @PostMapping
    public ResponseEntity<?> addFavoriteMovie(@RequestHeader("User-Id") String headerId,
                                              @RequestParam("user") long userId, @RequestParam("movie") long movieId) {

        log.debug("Adding movie {} as favorite for user {}", movieId, userId);
        // Check if the user is authorized
        if (!isAuthorized(headerId, userId)) {
            log.warn("User {} is not authorized to add a favorite movie", userId);
            return ResponseEntity.status(403).build();
        }
        // Add the movie to user's favorites
        boolean added = favoriteMovieService.addFavoriteMovie(userId, movieId);
        // Return appropriate response based on whether the movie was added successfully

        if (added) {
            log.info("Movie {} added as favorite for user {}", movieId, userId);
            return ResponseEntity.ok().build();
        }
        log.error("Failed to add movie {} as favorite for user {}", movieId, userId);
        return internalError();
    }

    /**
     * Endpoint to delete a favorite movie for a user.
     *
     * @param headerId the User-Id header
     * @param userId   the ID of the user
     * @param movieId  the ID of the movie to be deleted
     * @return ResponseEntity with status 200 if the movie is successfully deleted, 403 if not authorized, or an internal error
     */
    @DeleteMapping
    public ResponseEntity<?> deleteFavoriteMovie(@RequestHeader("User-Id") String headerId,
                                                 @RequestParam("user") long userId, @RequestParam("movie") long movieId) {

        log.debug("Deleting favorite movie for user {} and movie {}", userId, movieId);
        // Check if the user is authorized
        if (!isAuthorized(headerId, userId)) {
            log.warn("User {} is not authorized to delete favorite movie", userId);
            return ResponseEntity.status(403).build();
        }
        // Delete the favorite movie
        boolean deleted = favoriteMovieService.deleteFavoriteMovie(userId, movieId);

        log.debug("Favorite movie deletion status: {}", deleted);
        // Return appropriate response based on the deletion status
        if (deleted) {
            log.info("Favorite movie deleted successfully for user {}", userId);
            return ResponseEntity.ok().build();
        }
        log.error("Internal error occurred while deleting favorite movie for user {}", userId);
        return internalError();
    }

    /**
     * Get the list of favorite movies for the given user ID.
     *
     * @param headerId the User-Id header
     * @param userId   the user ID path parameter
     * @return the list of favorite movies for the user
     */
    @GetMapping("/{id}")
    public ResponseEntity<List<FavoriteMovie>> getFavoriteMovies(@RequestHeader("User-Id") String headerId,
                                                                 @PathVariable("id") long userId) {

        log.debug("Checking authorization for user with ID: {}", userId);

        if (!isAuthorized(headerId, userId)) {
            log.warn("User with ID: {} is not authorized to access favorite movies", userId);
            return ResponseEntity.status(403).build(); // Return forbidden status if not authorized
        }

        log.debug("Retrieving favorite movies for user with ID: {}", userId);
        List<FavoriteMovie> favoriteMovies = favoriteMovieService.getFavoriteMovies(userId);
        log.debug("Retrieved {} favorite movies for user with ID: {}", favoriteMovies.size(), userId);

        return ResponseEntity.ok(favoriteMovies); // Return the list of favorite movies
    }
    /**
     * Endpoint for discovering movies
     *
     * @param headerId   the User-Id header
     * @param userId     the user ID
     * @param loaderType the type of loader
     * @return ResponseEntity with a list of movies
     */
    @GetMapping("/discover")
    public ResponseEntity<List<Movie>> discoverMovies(@RequestHeader("User-Id") String headerId,
                                                      @RequestParam("user") long userId,
                                                      @RequestParam("loaderType") String loaderType) {


        log.debug("User ID: {}", userId);
        log.debug("Loader Type: {}", loaderType);

        // Check if the user is authorized
        if (!isAuthorized(headerId, userId)) {
            log.warn("Unauthorized access for User ID: {}", userId);
            return ResponseEntity.status(403).build();
        }

        // Retrieve the list of movies
        List<Movie> movies = favoriteMovieService.discoverMovies(userId, loaderType);
        log.info("Retrieved {} movies for User ID: {}", movies.size(), userId);
        return ResponseEntity.ok(movies);
    }


    /**
     * Check if the header ID matches the user ID.
     *
     * @param headerId The ID from the header
     * @param userId   The user's ID
     * @return true if the header ID matches the user ID, false otherwise
     */
    private boolean isAuthorized(String headerId, long userId) {
        return Long.parseLong(headerId) == userId;
    }

    /**
     * Generates a response entity with a 500 status code and a specific error message for internal server error.
     *
     * @return the response entity
     */
    private ResponseEntity<String> internalError() {
        String INTERNAL_SERVER_ERROR_MESSAGE = "{\"error\": \"INTERNAL_ERROR\"}";
        return ResponseEntity.status(500).body(INTERNAL_SERVER_ERROR_MESSAGE);
    }
}
