package com.recipeapi.recipeapi.service;
import com.recipeapi.recipeapi.exception.NotFoundException;
import com.recipeapi.recipeapi.model.Recipe;
import com.recipeapi.recipeapi.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Service class for managing Recipe operations.
 *
 * <p>This service provides the business logic for CRUD operations and search
 * functionality related to recipes. It acts as an intermediary between the
 * controller layer and the data access layer (repositories).</p>
 *
 * <p>The service implements various operations including:
 * <ul>
 *   <li>Creating new recipes</li>
 *   <li>Retrieving recipes (all, by ID, by category, etc.)</li>
 *   <li>Updating existing recipes</li>
 *   <li>Deleting recipes</li>
 *   <li>Searching recipes by different criteria</li>
 * </ul>
 * </p>
 *
 * @author Reina
 * @version 1.0
 */
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    /**
     * Constructs a new RecipeService with the specified repository.
     *
     * @param recipeRepository the repository for recipe data access
     */
    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    // Create a new recipe
    /**
     * Creates a new recipe.
     *
     * <p>If the createdBy field is null or empty, it sets a default value of "system".</p>
     *
     * @param recipe the recipe to create
     * @return the created recipe with generated ID
     */
    public Recipe createRecipe(Recipe recipe) {
        // Set default username if createdBy is null
        if (recipe.getCreatedBy() == null || recipe.getCreatedBy().isEmpty()) {
            recipe.setCreatedBy("system");
        }
        return recipeRepository.save(recipe);
    }

    // Get all recipes (no pagination)
    /**
     * Retrieves all recipes without pagination.
     *
     * @return a list of all recipes
     */
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    // Get all recipes with pagination
    /**
     * Retrieves all recipes with pagination support.
     *
     * @param pageable the pagination information
     * @return a page of recipes
     */
    public Page<Recipe> getAllRecipes(Pageable pageable) {
        return recipeRepository.findAll(pageable);
    }

    // Get a recipe by ID
    /**
     * Retrieves a recipe by its ID.
     *
     * @param id the ID of the recipe to retrieve
     * @return the found recipe
     * @throws NotFoundException if no recipe is found with the given ID
     */
    public Recipe getRecipeById(String id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recipe not found with id: " + id));
    }

    // Update a recipe
    /**
     * Updates an existing recipe.
     *
     * <p>Preserves the createdBy field if not provided in the update.</p>
     *
     * @param id the ID of the recipe to update
     * @param recipeDetails the new recipe details
     * @return the updated recipe
     * @throws NotFoundException if no recipe is found with the given ID
     */
    public Recipe updateRecipe(String id, Recipe recipeDetails) {
        Recipe recipe = getRecipeById(id);

        // Update the recipe fields
        recipe.setTitle(recipeDetails.getTitle());
        recipe.setIngredients(recipeDetails.getIngredients());
        recipe.setInstructions(recipeDetails.getInstructions());
        recipe.setCookingTime(recipeDetails.getCookingTime());
        recipe.setCategory(recipeDetails.getCategory());

        // Preserve the createdBy field if not set in the update
        if (recipeDetails.getCreatedBy() != null && !recipeDetails.getCreatedBy().isEmpty()) {
            recipe.setCreatedBy(recipeDetails.getCreatedBy());
        } else if (recipe.getCreatedBy() == null || recipe.getCreatedBy().isEmpty()) {
            recipe.setCreatedBy("system"); // Set default if missing
        }

        return recipeRepository.save(recipe);
    }

    // Delete a recipe
    /**
     * Deletes a recipe by its ID.
     *
     * @param id the ID of the recipe to delete
     * @throws NotFoundException if no recipe is found with the given ID
     */
    public void deleteRecipe(String id) {
        Recipe recipe = getRecipeById(id);
        recipeRepository.delete(recipe);
    }

    // Find recipes by category
    /**
     * Finds recipes by category.
     *
     * @param category the category to filter by
     * @return a list of recipes in the given category
     */
    public List<Recipe> getRecipesByCategory(String category) {
        return recipeRepository.findByCategory(category);
    }

    // Find recipes by category with pagination
    /**
     * Finds recipes by category with pagination support.
     *
     * @param category the category to filter by
     * @param pageable the pagination information
     * @return a page of recipes in the given category
     */
    public Page<Recipe> getRecipesByCategory(String category, Pageable pageable) {
        return recipeRepository.findByCategory(category, pageable);
    }

    // Find recipes by title containing the given text
    /**
     * Searches for recipes by title.
     *
     * <p>This method performs a case-insensitive search for recipes
     * whose titles contain the given text.</p>
     *
     * @param title the title substring to search for
     * @return a list of matching recipes
     */
    public List<Recipe> searchRecipesByTitle(String title) {
        return recipeRepository.findByTitleContainingIgnoreCase(title);
    }

    // Find recipes that contain a specific ingredient
    /**
     * Searches for recipes by ingredient.
     *
     * <p>This method finds recipes that contain the specified ingredient.</p>
     *
     * @param ingredient the ingredient to search for
     * @return a list of recipes containing the ingredient
     */
    public List<Recipe> searchRecipesByIngredient(String ingredient) {
        return recipeRepository.findByIngredient(ingredient);
    }

    // Find recipes with cooking time less than the provided value
    /**
     * Finds recipes with cooking time less than a specified value.
     *
     * @param maxCookingTime the maximum cooking time in minutes
     * @return a list of recipes with cooking time less than the specified value
     */
    public List<Recipe> getRecipesByCookingTimeLessThan(Integer maxCookingTime) {
        return recipeRepository.findByCookingTimeLessThan(maxCookingTime);
    }

    // Advanced search with multiple criteria
    /**
     * Performs an advanced search for recipes using multiple criteria.
     *
     * <p>This method searches for recipes where:
     * <ul>
     *   <li>The title or category matches the search term (case-insensitive)</li>
     *   <li>The cooking time is less than or equal to the maximum value</li>
     * </ul>
     * </p>
     *
     * @param searchTerm the search term for title and category
     * @param maxCookingTime the maximum cooking time in minutes
     * @param pageable the pagination information
     * @return a page of recipes matching the criteria
     */
    public Page<Recipe> searchRecipes(String searchTerm, Integer maxCookingTime, Pageable pageable) {
        return recipeRepository.findBySearchTermAndMaxCookingTime(searchTerm, maxCookingTime, pageable);
    }
}