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


    @GetMapping
    public ResponseEntity<?> getMovies(
            @RequestParam(defaultValue = "0") int page,  // page number
            @RequestParam(defaultValue = "15") int size  // number of items per page
    ) {
        // log the request parameters
        log.debug("Fetching movies with page: " + page + " and size: " + size);

        // retrieve movies from the service
        List<Movie> movies = movieService.getMovies(page, size);

        // log the number of movies fetched
        log.debug("Fetched " + movies.size() + " movies");

        // return the list of movies in a ResponseEntity
        return ResponseEntity.ok(movies);
    }
}
