
/**
 * Model package for the Recipe API application.
 *
 * <p>This package contains the domain model classes that represent the core
 * entities of the application. These models are mapped to MongoDB documents
 * and serve as the data transfer objects throughout the application.</p>
 *
 * <p>The main models in this package are:</p>
 * <ul>
 *   <li>{@link com.recipeapi.recipeapi.model.Recipe} - Represents a recipe with ingredients, instructions, etc.</li>
 *   <li>{@link com.recipeapi.recipeapi.model.User} - Represents a user with authentication details</li>
 * </ul>
 *
 * <p>Models in this package use Spring Data MongoDB annotations for document mapping
 * and validation annotations for ensuring data integrity.</p>
 *
 * @author Your Name
 * @version 1.0
 */
package com.recipeapi.recipeapi.model;