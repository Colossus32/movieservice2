package com.colossus.movieservice2.controller;

import com.colossus.movieservice2.entity.Movie;
import com.colossus.movieservice2.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieControllerTest {

    private MovieService movieService;
    private MovieController movieController;

    @BeforeEach
    void setUp() {
        movieService = mock(MovieService.class); // Create mock MovieService
        movieController = new MovieController(movieService); // Instantiate MovieController with the mock
    }

    @Test
    void getMovies_Success() {
        // Given
        int page = 0;
        int size = 10;
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie("Movie 1", "htttp://movie1.com"));
        movies.add(new Movie("Movie 2", "htttp://movie2.com"));

        // When
        when(movieService.getMovies(page, size)).thenReturn(movies);
        ResponseEntity<?> responseEntity = movieController.getMovies(page, size);

        // Then
        verify(movieService, times(1)).getMovies(page, size); // Verify that movieService.getMovies was called once with the given parameters
        assert responseEntity.getStatusCode().is2xxSuccessful(); // Assert that the response is successful
        assert responseEntity.getBody() == movies; // Assert that the returned list of movies matches the expected list
    }

    @Test
    void getMovies_EmptyList() {
        // Given
        int page = 0;
        int size = 10;
        List<Movie> movies = new ArrayList<>();

        // When
        when(movieService.getMovies(page, size)).thenReturn(movies);
        ResponseEntity<?> responseEntity = movieController.getMovies(page, size);

        // Then
        verify(movieService, times(1)).getMovies(page, size); // Verify that movieService.getMovies was called once with the given parameters
        assert responseEntity.getStatusCode().is2xxSuccessful(); // Assert that the response is successful
        assert responseEntity.getBody() == movies; // Assert that the returned list of movies matches the expected empty list
    }
}