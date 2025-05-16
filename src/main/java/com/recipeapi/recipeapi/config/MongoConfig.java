package com.recipeapi.recipeapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.recipeapi.recipeapi.repository")
public class MongoConfig {
    // Spring Boot will auto-configure MongoDB with application.properties
}