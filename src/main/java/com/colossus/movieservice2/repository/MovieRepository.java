package com.colossus.movieservice2.repository;

import com.colossus.movieservice2.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByNameRu(String nameRu);

    Optional<Movie> findByPosterUrl(String posterUrl);
}
