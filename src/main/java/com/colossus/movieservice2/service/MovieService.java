package com.colossus.movieservice2.service;

import com.colossus.movieservice2.entity.Movie;
import com.colossus.movieservice2.repository.MovieRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;

    @Value("${apikey}")
    private String apikey;

    @Value("${cronExpression}")
    String cronExpression;

    @Scheduled(cron = "${cronExpression}")
    private void updateMovieList() {
        try {
            int year = YearMonth.now().getYear();
            String month = YearMonth.now().getMonth().toString();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("https://kinopoiskapiunofficial.tech/api/v2.2/films/premieres?year=%d&month=%s", year, month)))
                    .header("accept", "application/json")
                    .header("X-API-KEY", apikey)
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            log.info(response.body());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.body());
            List<Movie> movies = objectMapper.readValue(jsonNode.get("items").toString(), new TypeReference<>() {});

            for (Movie movie : movies) {
                String nameRu = movie.getNameRu();
                String posterUrl = movie.getPosterUrl();
                Optional<Movie> existingMovieByNameRu = movieRepository.findByNameRu(nameRu);
                Optional<Movie> existingMovieByPosterUrl = movieRepository.findByPosterUrl(posterUrl);
                if (existingMovieByNameRu.isEmpty() && existingMovieByPosterUrl.isEmpty()) {
                    movieRepository.save(movie);
                }
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Movie> getMovies(int page, int quantity) {
        if (page < 0 || quantity < 0) {
            throw new IllegalArgumentException("Page and quantity must be greater than 0");
        }

        Page<Movie> movies = movieRepository.findAll(PageRequest.of(page, quantity));
        return movies.getContent();
    }
}
