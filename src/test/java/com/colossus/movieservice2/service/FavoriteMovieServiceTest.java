package com.colossus.movieservice2.service;

import com.colossus.movieservice2.entity.FavoriteMovie;
import com.colossus.movieservice2.entity.Movie;
import com.colossus.movieservice2.repository.FavoriteMovieRepository;
import com.colossus.movieservice2.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteMovieServiceTest {

    private FavoriteMovieService favoriteMovieService;
    private FavoriteMovieRepository favoriteMovieRepository;
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        favoriteMovieRepository = mock(FavoriteMovieRepository.class);
        movieRepository = mock(MovieRepository.class);
        favoriteMovieService = new FavoriteMovieService(favoriteMovieRepository, movieRepository);
    }

    @Test
    void addFavoriteMovie_Success() {
        // Given
        long userId = 1;
        long movieId = 1;
        when(favoriteMovieRepository.findByUserIdAndMovieId(userId, movieId)).thenReturn(Optional.empty());

        // When
        boolean result = favoriteMovieService.addFavoriteMovie(userId, movieId);

        // Then
        assert result; // The movie should be successfully added as a favorite
        verify(favoriteMovieRepository, times(1)).findByUserIdAndMovieId(userId, movieId);
        verify(favoriteMovieRepository, times(1)).save(new FavoriteMovie(userId, movieId));
    }

    @Test
    void addFavoriteMovie_AlreadyFavorite() {
        // Given
        long userId = 1;
        long movieId = 1;
        when(favoriteMovieRepository.findByUserIdAndMovieId(userId, movieId)).thenReturn(Optional.of(new FavoriteMovie(userId, movieId)));

        // When
        boolean result = favoriteMovieService.addFavoriteMovie(userId, movieId);

        // Then
        assert !result; // The movie is already a favorite
        verify(favoriteMovieRepository, times(1)).findByUserIdAndMovieId(userId, movieId);
        verify(favoriteMovieRepository, never()).save(any());
    }

    @Test
    void deleteFavoriteMovie_Success() {
        // Given
        long userId = 1;
        long movieId = 1;
        FavoriteMovie favoriteMovie = new FavoriteMovie(userId, movieId);
        when(favoriteMovieRepository.findByUserIdAndMovieId(userId, movieId)).thenReturn(Optional.of(favoriteMovie));

        // When
        boolean result = favoriteMovieService.deleteFavoriteMovie(userId, movieId);

        // Then
        assert result; // The movie should be successfully deleted
        verify(favoriteMovieRepository, times(1)).findByUserIdAndMovieId(userId, movieId);
        verify(favoriteMovieRepository, times(1)).delete(favoriteMovie);
    }

    @Test
    void deleteFavoriteMovie_MovieNotFound() {
        // Given
        long userId = 1;
        long movieId = 1;
        when(favoriteMovieRepository.findByUserIdAndMovieId(userId, movieId)).thenReturn(Optional.empty());

        // When
        boolean result = favoriteMovieService.deleteFavoriteMovie(userId, movieId);

        // Then
        assert !result; // The movie is not found, so deletion should fail
        verify(favoriteMovieRepository, times(1)).findByUserIdAndMovieId(userId, movieId);
        verify(favoriteMovieRepository, never()).delete(any());
    }

    @Test
    void getFavoriteMovies_ReturnsListOfMovies() {
        // Given
        long userId = 1;
        List<FavoriteMovie> favoriteMovies = new ArrayList<>();
        favoriteMovies.add(new FavoriteMovie(userId, 1));
        favoriteMovies.add(new FavoriteMovie(userId, 2));
        when(favoriteMovieRepository.findByUserId(userId)).thenReturn(favoriteMovies);

        // When
        List<FavoriteMovie> result = favoriteMovieService.getFavoriteMovies(userId);

        // Then
        assert result.size() == favoriteMovies.size(); // Ensure all favorite movies are returned
        verify(favoriteMovieRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getFavoriteMovies_NoMoviesFound() {
        // Given
        long userId = 1;
        when(favoriteMovieRepository.findByUserId(userId)).thenReturn(new ArrayList<>());

        // When
        List<FavoriteMovie> result = favoriteMovieService.getFavoriteMovies(userId);

        // Then
        assert result.isEmpty(); // Ensure empty list is returned when no movies found
        verify(favoriteMovieRepository, times(1)).findByUserId(userId);
    }

    @Test
    void discoverMovies_SQLLoader() {
        // Given
        long userId = 1;
        String loaderType = "sql";

        // When
        List<Movie> result = favoriteMovieService.discoverMovies(userId, loaderType);

        // Then
        assert result != null; // Ensure result is not null
        // Add additional assertions based on the behavior of discoverMoviesBySQL()
    }

    @Test
    void discoverMovies_InMemoryLoader() {
        // Given
        long userId = 1;
        String loaderType = "inMemory";

        // When
        List<Movie> result = favoriteMovieService.discoverMovies(userId, loaderType);

        // Then
        assert result != null; // Ensure result is not null
        // Add additional assertions based on the behavior of discoverMoviesInMemory()
    }

    @Test
    void discoverMovies_UnknownLoader() {
        // Given
        long userId = 1;
        String loaderType = "unknown";

        // When
        List<Movie> result = favoriteMovieService.discoverMovies(userId, loaderType);

        // Then
        assert result != null; // Ensure result is not null
        assert result.isEmpty(); // Ensure empty list is returned for unknown loader type
    }
}