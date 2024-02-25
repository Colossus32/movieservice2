package com.colossus.movieservice2.repository;

import com.colossus.movieservice2.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByNameRu(String nameRu);

    Optional<Movie> findByPosterUrl(String posterUrl);

    @Query("SELECT m FROM Movie m where m.id NOT IN (SELECT fm.movieId FROM FavoriteMovie fm WHERE fm.userId = :userId)")
    List<Movie> discoverMoviesBySQL(@Param("userId") long userId);
}
