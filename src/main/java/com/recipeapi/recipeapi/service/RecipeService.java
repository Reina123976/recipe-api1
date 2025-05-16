package com.recipeapi.recipeapi.service;
import com.recipeapi.recipeapi.exception.NotFoundException;
import com.recipeapi.recipeapi.model.Recipe;
import com.recipeapi.recipeapi.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    // Create a new recipe
    public Recipe createRecipe(Recipe recipe) {
        // Set default username if createdBy is null
        if (recipe.getCreatedBy() == null || recipe.getCreatedBy().isEmpty()) {
            recipe.setCreatedBy("system");
        }
        return recipeRepository.save(recipe);
    }

    // Get all recipes (no pagination)
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    // Get all recipes with pagination
    public Page<Recipe> getAllRecipes(Pageable pageable) {
        return recipeRepository.findAll(pageable);
    }

    // Get a recipe by ID
    public Recipe getRecipeById(String id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recipe not found with id: " + id));
    }

    // Update a recipe
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
    public void deleteRecipe(String id) {
        Recipe recipe = getRecipeById(id);
        recipeRepository.delete(recipe);
    }

    // Find recipes by category
    public List<Recipe> getRecipesByCategory(String category) {
        return recipeRepository.findByCategory(category);
    }

    // Find recipes by category with pagination
    public Page<Recipe> getRecipesByCategory(String category, Pageable pageable) {
        return recipeRepository.findByCategory(category, pageable);
    }

    // Find recipes by title containing the given text
    public List<Recipe> searchRecipesByTitle(String title) {
        return recipeRepository.findByTitleContainingIgnoreCase(title);
    }

    // Find recipes that contain a specific ingredient
    public List<Recipe> searchRecipesByIngredient(String ingredient) {
        return recipeRepository.findByIngredient(ingredient);
    }

    // Find recipes with cooking time less than the provided value
    public List<Recipe> getRecipesByCookingTimeLessThan(Integer maxCookingTime) {
        return recipeRepository.findByCookingTimeLessThan(maxCookingTime);
    }

    // Advanced search with multiple criteria
    public Page<Recipe> searchRecipes(String searchTerm, Integer maxCookingTime, Pageable pageable) {
        return recipeRepository.findBySearchTermAndMaxCookingTime(searchTerm, maxCookingTime, pageable);
    }
}