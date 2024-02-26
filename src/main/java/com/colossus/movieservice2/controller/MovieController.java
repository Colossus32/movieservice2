package com.colossus.movieservice2.controller;

import com.colossus.movieservice2.entity.Movie;
import com.colossus.movieservice2.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/movies")
@RequiredArgsConstructor
@Slf4j
public class MovieController {

    private final MovieService movieService;

    private static final String PAGE = "0";
    private static final String SIZE = "15";


    /**
     * Get a list of movies with pagination
     *
     * @param page page number
     * @param size number of items per page
     * @return ResponseEntity with the list of movies
     */
    @GetMapping
    public ResponseEntity<List<Movie>> getMovies(
            @RequestParam(defaultValue = PAGE) int page,  // page number
            @RequestParam(defaultValue = SIZE) int size  // number of items per page
    ) {
        log.debug("Fetching movies with page: " + page + " and size: " + size);

        List<Movie> movies = movieService.getMovies(page, size);

        log.debug("Fetched " + movies.size() + " movies");
        // return the list of movies in a ResponseEntity
        return ResponseEntity.ok(movies);
    }
}
