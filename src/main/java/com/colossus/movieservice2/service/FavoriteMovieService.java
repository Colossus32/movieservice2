package com.colossus.movieservice2.service;

import com.colossus.movieservice2.entity.FavoriteMovie;
import com.colossus.movieservice2.entity.Movie;
import com.colossus.movieservice2.repository.FavoriteMovieRepository;
import com.colossus.movieservice2.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteMovieService {

    private final FavoriteMovieRepository favoriteMovieRepository;
    private final MovieRepository movieRepository;

    /**
     * Add the movie with the given ID to the user's list of favorite movies
     * @param userId the ID of the user
     * @param movieId the ID of the movie to be added as a favorite
     * @return true if the movie was successfully added as a favorite, false if it was already a favorite
     */
    public boolean addFavoriteMovie(long userId, long movieId) {
        Optional<FavoriteMovie> movieByUserAndMovie = favoriteMovieRepository.findByUserIdAndMovieId(userId, movieId);
        if (movieByUserAndMovie.isPresent()) {
            log.debug("Movie with ID {} is already a favorite for user with ID {}", movieId, userId);
            return false;
        }
        favoriteMovieRepository.save(new FavoriteMovie(userId, movieId));
        log.debug("Movie with ID {} added as a favorite for user with ID {}", movieId, userId);
        return true;
    }


    /**
     * Deletes a favorite movie for a user.
     *
     * @param userId the ID of the user
     * @param movieId the ID of the movie
     * @return true if the movie was deleted, false if the movie was not found
     */
    public boolean deleteFavoriteMovie(long userId, long movieId) {
        Optional<FavoriteMovie> movieByUserAndMovie = favoriteMovieRepository.findByUserIdAndMovieId(userId, movieId);

        // log if the movie is not found
        if (movieByUserAndMovie.isEmpty()) {
            log.info("Movie not found for user {} and movie {}", userId, movieId);
            return false;
        }

        // delete the movie and log the action
        favoriteMovieRepository.delete(movieByUserAndMovie.get());
        log.info("Deleted favorite movie for user {} and movie {}", userId, movieId);
        return true;
    }

    /**
     * Retrieve favorite movies for a given user.
     *
     * @param userId the ID of the user
     * @return the list of favorite movies for the user
     */
    public List<FavoriteMovie> getFavoriteMovies(long userId) {
        log.debug("Retrieving favorite movies for user with ID: " + userId);
        List<FavoriteMovie> favoriteMovies = favoriteMovieRepository.findByUserId(userId);
        log.debug("Retrieved " + favoriteMovies.size() + " favorite movies for user with ID: " + userId);
        return favoriteMovies;
    }

    /**
     * Discover movies based on the loader type.
     *
     * @param userId the user ID
     * @param loaderType the type of loader to use (e.g. "sql" or "inMemory")
     * @return a list of discovered movies
     */
    public List<Movie> discoverMovies(long userId, String loaderType) {
        log.debug("Discovering movies for user {} using loader type {}", userId, loaderType);
        List<Movie> discoveredMovies = switch (loaderType) {
            case "sql" -> discoverMoviesBySQL(userId);
            case "inMemory" -> discoverMoviesInMemory(userId);
            default -> List.of();
        };
        log.debug("Discovered {} movies using loader type {}", discoveredMovies.size(), loaderType);
        return discoveredMovies;
    }

    /**
     * Get movie recommendations for a user using SQL query.
     *
     * @param userId The ID of the user for whom to discover movies
     * @return List of recommended movies
     */
    private List<Movie> discoverMoviesBySQL(long userId) {
        log.debug("Discovering movies for user with ID: " + userId);
        List<Movie> recommendedMovies = movieRepository.discoverMoviesBySQL(userId);
        log.debug("Found " + recommendedMovies.size() + " recommended movies for user with ID: " + userId);
        return recommendedMovies;
    }

    /**
     * Retrieves a list of movies that the specified user has not favorited.
     *
     * @param userId The ID of the user
     * @return A list of movies not favorited by the user
     */
    private List<Movie> discoverMoviesInMemory(long userId) {
        // Retrieve all movies
        List<Movie> movies = movieRepository.findAll();

        log.debug("Retrieved {} movies", movies.size());

        // Retrieve favorite movies of the user
        List<FavoriteMovie> favoriteMovies = favoriteMovieRepository.findByUserId(userId);

        log.debug("Retrieved {} favorite movies for user with ID {}", favoriteMovies.size(), userId);

        // Extract IDs of the favorite movies
        Set<Long> favoriteMovieIds = favoriteMovies.stream().map(FavoriteMovie::getMovieId).collect(Collectors.toSet());

        log.debug("Favorite movie IDs: {}", favoriteMovieIds);

        // Filter out the movies that have been favorited by the user
        List<Movie> notFavoritedMovies = movies.stream()
                .filter(movie -> !favoriteMovieIds.contains(movie.getId()))
                .collect(Collectors.toList());

        log.debug("Retrieved {} not favorited movies for user with ID {}", notFavoritedMovies.size(), userId);

        return notFavoritedMovies;
    }


}
