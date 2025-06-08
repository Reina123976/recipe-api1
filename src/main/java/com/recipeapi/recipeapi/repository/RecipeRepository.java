package com.recipeapi.recipeapi.repository;
import com.recipeapi.recipeapi.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Recipe entities.
 *
 * <p>This interface provides methods for CRUD operations on recipes,
 * as well as custom queries for searching and filtering recipes.</p>
 *
 * @author Your Name
 * @version 1.0
 */

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {

    // Find recipes by category
    /**
     * Finds recipes by category.
     *
     * @param category The category to filter by
     * @return List of recipes in the given category
     */
    List<Recipe> findByCategory(String category);

    // Find recipes by title containing the given text (case insensitive)
    /**
     * Finds recipes by title containing the given text (case insensitive).
     *
     * @param title The title substring to search for
     * @return List of recipes with matching titles
     */
    List<Recipe> findByTitleContainingIgnoreCase(String title);

    // Find recipes that contain a specific ingredient
    /**
     * Finds recipes that contain a specific ingredient.
     *
     * @param ingredient The ingredient to search for
     * @return List of recipes containing the ingredient
     */
    @Query("{ 'ingredients': { $regex: ?0, $options: 'i' } }")
    List<Recipe> findByIngredient(String ingredient);

    // Find recipes with cooking time less than the provided value
    /**
     * Finds recipes with cooking time less than the provided value.
     *
     * @param maxCookingTime The maximum cooking time in minutes
     * @return List of recipes with cooking time less than maxCookingTime
     */
    List<Recipe> findByCookingTimeLessThan(Integer maxCookingTime);

    // Implement pagination for all recipes
    /**
     * Finds all recipes with pagination.
     *
     * @param pageable Pagination information
     * @return Page of recipes
     */
    Page<Recipe> findAll(Pageable pageable);

    // Implement pagination for recipes by category
    /**
     * Finds recipes by category with pagination.
     *
     * @param category The category to filter by
     * @param pageable Pagination information
     * @return Page of recipes in the given category
     */
    Page<Recipe> findByCategory(String category, Pageable pageable);

    // Advanced search with multiple criteria
    /**
     * Advanced search for recipes with multiple criteria.
     *
     * <p>This method searches for recipes where:
     * <ul>
     *   <li>Title or category matches the search term (case insensitive)</li>
     *   <li>Cooking time is less than or equal to the maximum value</li>
     * </ul>
     * </p>
     *
     * @param searchTerm The search term for title and category
     * @param maxCookingTime The maximum cooking time in minutes
     * @param pageable Pagination information
     * @return Page of recipes matching the criteria
     */
    @Query("{ $and: [ " +
            "{ $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'category': { $regex: ?0, $options: 'i' } } ] }, " +
            "{ 'cookingTime': { $lte: ?1 } } " +
            "] }")
    Page<Recipe> findBySearchTermAndMaxCookingTime(String searchTerm, Integer maxCookingTime, Pageable pageable);
}