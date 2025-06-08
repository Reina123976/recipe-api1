package com.recipeapi.recipeapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration class for the Recipe API application.
 *
 * <p>This class enables Spring Data MongoDB repositories and defines
 * the base package where repositories are located.</p>
 *
 * <p>Additional MongoDB configuration is provided in application.properties.</p>
 *
 * @author Reinakazan
 * @version 1.0
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.recipeapi.recipeapi.repository")
public class MongoConfig {
    // Spring Boot will auto-configure MongoDB with application.properties
}