package com.colossus.movieservice2.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "favorite_movie")
public class FavoriteMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private long movieId;

    public FavoriteMovie(long userId, long movieId) {
        this.userId = userId;
        this.movieId = movieId;
    }
}
