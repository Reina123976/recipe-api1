package com.recipeapi.recipeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
/**
 * Main application class for the Recipe API.
 * This class bootstraps the Spring Boot application and enables MongoDB auditing.
 *
 * <p>The application provides RESTful endpoints for managing recipes, user authentication,
 * and user registration.</p>
 *
 * @author ReinaKazan
 * @version 1.0
 */
@SpringBootApplication
@EnableMongoAuditing
public class RecipeApiApplication {

    /**
     * Main method that starts the Spring Boot application.
     *
     * @param args Command line arguments passed to the application
     */

    public static void main(String[] args) {
        SpringApplication.run(RecipeApiApplication.class, args);
    }
}