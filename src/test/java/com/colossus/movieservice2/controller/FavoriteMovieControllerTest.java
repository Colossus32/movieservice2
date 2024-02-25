package com.colossus.movieservice2.controller;

import com.colossus.movieservice2.entity.FavoriteMovie;
import com.colossus.movieservice2.entity.Movie;
import com.colossus.movieservice2.service.FavoriteMovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteMovieControllerTest {

    private FavoriteMovieService favoriteMovieService;
    private FavoriteMovieController favoriteMovieController;

    @BeforeEach
    void setUp() {
        favoriteMovieService = mock(FavoriteMovieService.class);
        favoriteMovieController = new FavoriteMovieController(favoriteMovieService);
    }

    @Test
    void addFavoriteMovie_Success() {
        // Given
        String headerId = "123";
        long userId = 123;
        long movieId = 789;

        // When
        when(favoriteMovieService.addFavoriteMovie(userId, movieId)).thenReturn(true);
        ResponseEntity<?> responseEntity = favoriteMovieController.addFavoriteMovie(headerId, userId, movieId);

        // Then
        verify(favoriteMovieService, times(1)).addFavoriteMovie(userId, movieId); // Verify that favoriteMovieService.addFavoriteMovie was called once with the given parameters
        assert responseEntity.getStatusCode().is2xxSuccessful(); // Assert that the response is successful
    }

    @Test
    void addFavoriteMovie_Unauthorized() {
        // Given
        String headerId = "123";
        long userId = 456;
        long movieId = 789;

        // When
        ResponseEntity<?> responseEntity = favoriteMovieController.addFavoriteMovie(headerId, userId, movieId);

        // Then
        // Verify that favoriteMovieService.addFavoriteMovie is not called since the user is unauthorized
        verify(favoriteMovieService, never()).addFavoriteMovie(anyLong(), anyLong());
        assert responseEntity.getStatusCode().is4xxClientError(); // Assert that the response is forbidden (403)
    }

    @Test
    void addFavoriteMovie_Failure() {
        // Given
        String headerId = "123";
        long userId = 123;
        long movieId = 789;

        // When
        when(favoriteMovieService.addFavoriteMovie(userId, movieId)).thenReturn(false);
        ResponseEntity<?> responseEntity = favoriteMovieController.addFavoriteMovie(headerId, userId, movieId);

        // Then
        verify(favoriteMovieService, times(1)).addFavoriteMovie(userId, movieId); // Verify that favoriteMovieService.addFavoriteMovie was called once with the given parameters
        assert responseEntity.getStatusCode().is5xxServerError(); // Assert that the response indicates internal server error (500)
    }

    @Test
    void deleteFavoriteMovie_Success() {
        // Given
        String headerId = "123";
        long userId = 123;
        long movieId = 789;

        // When
        when(favoriteMovieService.deleteFavoriteMovie(userId, movieId)).thenReturn(true);
        ResponseEntity<?> responseEntity = favoriteMovieController.deleteFavoriteMovie(headerId, userId, movieId);

        // Then
        verify(favoriteMovieService, times(1)).deleteFavoriteMovie(userId, movieId);
        assert responseEntity.getStatusCode().is2xxSuccessful();
    }

    @Test
    void deleteFavoriteMovie_Unauthorized() {
        // Given
        String headerId = "123";
        long userId = 456;
        long movieId = 789;

        // When
        ResponseEntity<?> responseEntity = favoriteMovieController.deleteFavoriteMovie(headerId, userId, movieId);

        // Then
        verify(favoriteMovieService, never()).deleteFavoriteMovie(anyLong(), anyLong());
        assert responseEntity.getStatusCode().is4xxClientError();
    }

    @Test
    void deleteFavoriteMovie_Failure() {
        // Given
        String headerId = "123";
        long userId = 123;
        long movieId = 789;

        // When
        when(favoriteMovieService.deleteFavoriteMovie(userId, movieId)).thenReturn(false);
        ResponseEntity<?> responseEntity = favoriteMovieController.deleteFavoriteMovie(headerId, userId, movieId);

        // Then
        verify(favoriteMovieService, times(1)).deleteFavoriteMovie(userId, movieId);
        assert responseEntity.getStatusCode().is5xxServerError();
    }

    @Test
    void getFavoriteMovies_Authorized() {
        // Given
        String headerId = "123";
        long userId = 123;
        List<FavoriteMovie> favoriteMovies = new ArrayList<>();
        favoriteMovies.add(new FavoriteMovie(1L, 110L));
        favoriteMovies.add(new FavoriteMovie(2L, 120L));

        // When
        when(favoriteMovieService.getFavoriteMovies(userId)).thenReturn(favoriteMovies);
        ResponseEntity<List<FavoriteMovie>> responseEntity = favoriteMovieController.getFavoriteMovies(headerId, userId);

        // Then
        verify(favoriteMovieService, times(1)).getFavoriteMovies(userId);
        assert responseEntity.getStatusCode().is2xxSuccessful();
        assert Objects.equals(responseEntity.getBody(), favoriteMovies);
    }

    @Test
    void getFavoriteMovies_Unauthorized() {
        // Given
        String headerId = "123";
        long userId = 456;

        // When
        ResponseEntity<List<FavoriteMovie>> responseEntity = favoriteMovieController.getFavoriteMovies(headerId, userId);

        // Then
        verify(favoriteMovieService, never()).getFavoriteMovies(anyLong());
        assert responseEntity.getStatusCode().is4xxClientError();
    }

    @Test
    void discoverMovies_Authorized() {
        // Given
        String headerId = "123";
        long userId = 123;
        String loaderType = "sql";
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie("Movie 1", "https://example.com/movie1.jpg"));
        movies.add(new Movie("Movie 2", "https://example.com/movie2.jpg"));

        // When
        when(favoriteMovieService.discoverMovies(userId, loaderType)).thenReturn(movies);
        ResponseEntity<List<Movie>> responseEntity = favoriteMovieController.discoverMovies(headerId, userId, loaderType);

        // Then
        verify(favoriteMovieService, times(1)).discoverMovies(userId, loaderType);
        assert responseEntity.getStatusCode().is2xxSuccessful();
        assert Objects.equals(responseEntity.getBody(), movies);
    }

    @Test
    void discoverMovies_Unauthorized() {
        // Given
        String headerId = "123";
        long userId = 456;
        String loaderType = "type";

        // When
        ResponseEntity<List<Movie>> responseEntity = favoriteMovieController.discoverMovies(headerId, userId, loaderType);

        // Then
        verify(favoriteMovieService, never()).discoverMovies(anyLong(), anyString());
        assert responseEntity.getStatusCode().is4xxClientError();
    }
}