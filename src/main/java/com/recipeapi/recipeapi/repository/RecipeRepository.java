package com.recipeapi.recipeapi.repository;
import com.recipeapi.recipeapi.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {

    // Find recipes by category
    List<Recipe> findByCategory(String category);

    // Find recipes by title containing the given text (case insensitive)
    List<Recipe> findByTitleContainingIgnoreCase(String title);

    // Find recipes that contain a specific ingredient
    @Query("{ 'ingredients': { $regex: ?0, $options: 'i' } }")
    List<Recipe> findByIngredient(String ingredient);

    // Find recipes with cooking time less than the provided value
    List<Recipe> findByCookingTimeLessThan(Integer maxCookingTime);

    // Implement pagination for all recipes
    Page<Recipe> findAll(Pageable pageable);

    // Implement pagination for recipes by category
    Page<Recipe> findByCategory(String category, Pageable pageable);

    // Advanced search with multiple criteria
    @Query("{ $and: [ " +
            "{ $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'category': { $regex: ?0, $options: 'i' } } ] }, " +
            "{ 'cookingTime': { $lte: ?1 } } " +
            "] }")
    Page<Recipe> findBySearchTermAndMaxCookingTime(String searchTerm, Integer maxCookingTime, Pageable pageable);
}