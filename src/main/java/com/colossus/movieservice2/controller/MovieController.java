package com.colossus.movieservice2.controller;

import com.colossus.movieservice2.entity.Movie;
import com.colossus.movieservice2.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;


    @GetMapping
    public ResponseEntity<?> getMovies(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "15" ) int size) {

        List<Movie> movies = movieService.getMovies(page, size);
        return ResponseEntity.ok(movies);
    }

}
