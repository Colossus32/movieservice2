package com.colossus.movieservice2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Movieservice2Application {

    public static void main(String[] args) {
        SpringApplication.run(Movieservice2Application.class, args);
    }

}
