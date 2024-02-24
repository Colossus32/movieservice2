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

    /**
     * This method is scheduled to update the movie list using the Kinopoisk API.
     * It sends a request to the API to fetch the list of premieres for the current year and month,
     * processes the response, and saves new movies to the database.
     */
    @Scheduled(cron = "${cronExpression}")
    void updateMovieList() {
        try {
            // Get the current year and month
            int year = YearMonth.now().getYear();
            String month = YearMonth.now().getMonth().toString();

            // Build the HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("https://kinopoiskapiunofficial.tech/api/v2.2/films/premieres?year=%d&month=%s", year, month)))
                    .header("accept", "application/json")
                    .header("X-API-KEY", apikey)
                    .GET()
                    .build();

            log.debug("Sending request to movie API");

            // Send the HTTP request and receive the response
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Received response from movie API: {}", response.body());

            // Process the JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.body());
            List<Movie> movies = objectMapper.readValue(jsonNode.get("items").toString(), new TypeReference<>() {});

            // Save new movies to the database
            for (Movie movie : movies) {
                String nameRu = movie.getNameRu();
                String posterUrl = movie.getPosterUrl();
                Optional<Movie> existingMovieByNameRu = movieRepository.findByNameRu(nameRu);
                Optional<Movie> existingMovieByPosterUrl = movieRepository.findByPosterUrl(posterUrl);
                if (existingMovieByNameRu.isEmpty() && existingMovieByPosterUrl.isEmpty()) {
                    movieRepository.save(movie);
                    log.info("Saved new movie: {}", movie.getNameRu());
                }
            }

        } catch (IOException | InterruptedException e) {
            log.error("Error occurred while updating movie list", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a list of movies based on the specified page and quantity.
     *
     * @param page the page number
     * @param quantity the quantity of movies to retrieve
     * @return a list of movies
     * @throws IllegalArgumentException if page or quantity is less than 0
     */
    public List<Movie> getMovies(int page, int quantity) {
        if (page < 0 || quantity < 0) {
            throw new IllegalArgumentException("Page and quantity must be greater than 0");
        }

        log.debug("Fetching movies from page {} with quantity {}", page, quantity);
        Page<Movie> movies = movieRepository.findAll(PageRequest.of(page, quantity));
        log.debug("Retrieved {} movies", movies.getNumberOfElements());

        return movies.getContent();
    }
}
