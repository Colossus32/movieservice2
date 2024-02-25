package com.colossus.movieservice2.service;

import com.colossus.movieservice2.entity.Movie;
import com.colossus.movieservice2.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@PrepareForTest({HttpClient.class, HttpRequest.class, HttpResponse.class})
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMovies_ValidPageAndQuantity() {
        // Prepare test data
        int page = 0;
        int quantity = 10;
        List<Movie> expectedMovies = List.of(
                new Movie("Movie 1", "posterUrl1"),
                new Movie("Movie 2", "posterUrl2")
        );
        Page<Movie> pageOfMovies = new PageImpl<>(expectedMovies);

        // Mock repository behavior
        when(movieRepository.findAll(PageRequest.of(page, quantity))).thenReturn(pageOfMovies);

        // Call the method under test
        List<Movie> result = movieService.getMovies(page, quantity);

        // Verify that repository method is called with the expected arguments
        verify(movieRepository).findAll(PageRequest.of(page, quantity));

        // Assert that the returned list of movies matches the expected list
        assertEquals(expectedMovies, result);
    }

    @Test
    public void testGetMovies_InvalidPageOrQuantity() {
        // Prepare test data
        int invalidPage = -1;
        int invalidQuantity = -10;

        // Call the method under test and assert that it throws IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> movieService.getMovies(invalidPage, invalidQuantity));

        // Verify that repository method is not called
        verifyNoInteractions(movieRepository);
    }

    @Test
    void updateMovieList_SaveNewMovies() {
        // todo: implement complicated test
    }
}