package com.colossus.movieservice2.repository;

import com.colossus.movieservice2.entity.FavoriteMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteMovieRepository extends JpaRepository<FavoriteMovie, Long> {

    Optional<FavoriteMovie> findByUserIdAndMovieId(long userId, long movieId);
    List<FavoriteMovie> findByUserId(long userId);
}
