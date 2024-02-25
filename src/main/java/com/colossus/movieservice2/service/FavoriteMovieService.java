package com.colossus.movieservice2.service;

import com.colossus.movieservice2.entity.FavoriteMovie;
import com.colossus.movieservice2.entity.Movie;
import com.colossus.movieservice2.repository.FavoriteMovieRepository;
import com.colossus.movieservice2.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteMovieService {

    private final FavoriteMovieRepository favoriteMovieRepository;
    private final MovieRepository movieRepository;

    public boolean addFavoriteMovie(long userId, long movieId) {
        Optional<FavoriteMovie> movieByUserAndMovie = favoriteMovieRepository.findByUserIdAndMovieId(userId, movieId);
        if (movieByUserAndMovie.isPresent()) {
            return false;
        }
        favoriteMovieRepository.save(new FavoriteMovie(userId, movieId));
        return true;
    }


    public boolean deleteFavoriteMovie(long userId, long movieId) {
        Optional<FavoriteMovie> movieByUserAndMovie = favoriteMovieRepository.findByUserIdAndMovieId(userId, movieId);
        if (movieByUserAndMovie.isEmpty()) {
            return false;
        }
        favoriteMovieRepository.delete(movieByUserAndMovie.get());
        return true;
    }

    public List<FavoriteMovie> getFavoriteMovies(long userId) {
        return favoriteMovieRepository.findByUserId(userId);
    }

    public List<Movie> discoverMovies(long userId, String loaderType) {
        
        switch (loaderType) {
            case "sql":
                return discoverMoviesBySQL(userId);
            case "inMemory":
                return discoverMoviesInMemory(userId);
            default:
                return List.of();
        }
    }

    private List<Movie> discoverMoviesBySQL(long userId) {
        return movieRepository.discoverMoviesBySQL(userId);
    }

    private List<Movie> discoverMoviesInMemory(long userId) {
        List<Movie> movies = movieRepository.findAll();
        List<FavoriteMovie> favoriteMovies = favoriteMovieRepository.findByUserId(userId);
        Set<Long> favoriteMovieIds = favoriteMovies.stream().map(FavoriteMovie::getMovieId).collect(Collectors.toSet());
        
        return movies.stream()
                .filter(movie -> !favoriteMovieIds.contains(movie.getId()))
                .collect(Collectors.toList());
    }


}
