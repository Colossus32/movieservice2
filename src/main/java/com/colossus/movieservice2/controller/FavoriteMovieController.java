package com.colossus.movieservice2.controller;

import com.colossus.movieservice2.entity.FavoriteMovie;
import com.colossus.movieservice2.entity.Movie;
import com.colossus.movieservice2.service.FavoriteMovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favorites")
public class FavoriteMovieController {

    private final FavoriteMovieService favoriteMovieService;

    @PostMapping
    public ResponseEntity<?> addFavoriteMovie(@RequestHeader("User-Id") String headerId,
                                           @RequestParam("user") long userId, @RequestParam("movie") long movieId) {

        if (!isAuthorized(headerId, userId)) {
            return ResponseEntity.status(403).build();
        }

        boolean added = favoriteMovieService.addFavoriteMovie(userId, movieId);

        if (added) {
            return ResponseEntity.ok().build();
        }
        return internalError();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteFavoriteMovie(@RequestHeader("User-Id") String headerId,
                                                 @RequestParam("user") long userId, @RequestParam("movie") long movieId) {

        if (!isAuthorized(headerId, userId)) {
            return ResponseEntity.status(403).build();
        }

        boolean deleted = favoriteMovieService.deleteFavoriteMovie(userId, movieId);

        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return internalError();
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<FavoriteMovie>> getFavoriteMovies(@RequestHeader("User-Id") String headerId,
                                                         @PathVariable("id") long userId) {

        if (!isAuthorized(headerId, userId)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(favoriteMovieService.getFavoriteMovies(userId));

    }

    @GetMapping("/discover")
    public ResponseEntity<List<Movie>> discoverMovies(@RequestHeader("User-Id") String headerId,
                                                      @RequestParam("user")long userId,
                                                      @RequestParam("loaderType")String loaderType) {

        if (!isAuthorized(headerId, userId)) {
            return ResponseEntity.status(403).build();
        }

        List<Movie> movies = favoriteMovieService.discoverMovies(userId, loaderType);
        return ResponseEntity.ok(movies);
    }


    private boolean isAuthorized(String headerId, long userId) {
        return Long.parseLong(headerId) == userId;
    }

    private ResponseEntity<String> internalError() {
        String INTERNAL_SERVER_ERROR_MESSAGE = "{\"error\": \"INTERNAL_ERROR\"}";
        return ResponseEntity.status(500).body(INTERNAL_SERVER_ERROR_MESSAGE);
    }
}
