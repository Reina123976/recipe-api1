package com.recipeapi.recipeapi.controller;
import com.recipeapi.recipeapi.model.Recipe;
import com.recipeapi.recipeapi.service.RecipeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for recipe operations.
 *
 * <p>This controller provides endpoints for:
 * <ul>
 *   <li>Creating, reading, updating and deleting recipes</li>
 *   <li>Searching and filtering recipes</li>
 *   <li>Pagination and sorting of recipe lists</li>
 * </ul>
 * </p>
 *
 * @author ReinaKazan
 * @version 1.0
 */
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    /**
     * Creates a new RecipeController with the necessary dependencies.
     *
     * @param recipeService Service for recipe operations
     */
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // Create a new recipe
    /**
     * Creates a new recipe.
     *
     * <p>This endpoint accepts recipe data and persists it to the database.</p>
     *
     * @param recipe The recipe to create
     * @return ResponseEntity containing the created recipe
     */
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@Valid @RequestBody Recipe recipe) {
        Recipe createdRecipe = recipeService.createRecipe(recipe);
        return new ResponseEntity<>(createdRecipe, HttpStatus.CREATED);
    }

    // Helper method to create response map from Page object (to avoid code duplication)
    /**
     * Helper method to create a pagination response.
     *
     * <p>Formats a Page object into a Map containing pagination metadata and recipe content.</p>
     *
     * @param recipePage The Page object to format
     * @return A map containing recipes and pagination metadata
     */
    private Map<String, Object> createPaginationResponse(Page<Recipe> recipePage) {
        Map<String, Object> response = new HashMap<>();
        response.put("recipes", recipePage.getContent());
        response.put("currentPage", recipePage.getNumber());
        response.put("totalItems", recipePage.getTotalElements());
        response.put("totalPages", recipePage.getTotalPages());
        return response;
    }

    // Get all recipes with pagination and sorting
    /**
     * Retrieves all recipes with pagination and sorting.
     *
     * <p>This endpoint returns recipes with optional pagination and sorting parameters.</p>
     *
     * @param page The page number (0-indexed)
     * @param size The page size
     * @param sortBy The field to sort by
     * @param direction The sort direction ("asc" or "desc")
     * @return ResponseEntity containing recipes and pagination metadata
     */
    @GetMapping
    public ResponseEntity<?> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Recipe> recipePage = recipeService.getAllRecipes(pageable);

        // Modified to return just recipes array when requested from client
        if (size == Integer.MAX_VALUE || size >= 1000) {
            // Client is requesting all recipes
            return new ResponseEntity<>(recipePage.getContent(), HttpStatus.OK);
        } else {
            // Regular pagination response for web clients
            return new ResponseEntity<>(createPaginationResponse(recipePage), HttpStatus.OK);
        }
    }

    // Get a recipe by ID
    /**
     * Retrieves a recipe by ID.
     *
     * @param id The ID of the recipe to retrieve
     * @return ResponseEntity containing the recipe
     */
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable String id) {
        Recipe recipe = recipeService.getRecipeById(id);
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }

    // Update a recipe
    /**
     * Updates a recipe.
     *
     * <p>This endpoint updates an existing recipe with new data.</p>
     *
     * @param id The ID of the recipe to update
     * @param recipe The updated recipe data
     * @return ResponseEntity containing the updated recipe
     */
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable String id, @Valid @RequestBody Recipe recipe) {
        Recipe updatedRecipe = recipeService.updateRecipe(id, recipe);
        return new ResponseEntity<>(updatedRecipe, HttpStatus.OK);
    }

    // Delete a recipe
    /**
     * Deletes a recipe.
     *
     * <p>This endpoint deletes a recipe by ID.</p>
     *
     * @param id The ID of the recipe to delete
     * @return ResponseEntity confirming deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteRecipe(@PathVariable String id) {
        recipeService.deleteRecipe(id);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get recipes by category with pagination
    /**
     * Retrieves recipes by category with pagination.
     *
     * @param category The category to filter by
     * @param page The page number (0-indexed)
     * @param size The page size
     * @return ResponseEntity containing matching recipes and pagination metadata
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Map<String, Object>> getRecipesByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipePage = recipeService.getRecipesByCategory(category, pageable);

        return new ResponseEntity<>(createPaginationResponse(recipePage), HttpStatus.OK);
    }

    // Add search endpoint for recipe titles
    /**
     * Searches for recipes by title.
     *
     * @param title The title substring to search for
     * @return ResponseEntity containing matching recipes
     */
    @GetMapping("/search/title")
    public ResponseEntity<List<Recipe>> searchRecipesByTitle(@RequestParam String title) {
        List<Recipe> recipes = recipeService.searchRecipesByTitle(title);
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    // Add search endpoint for ingredients
    /**
     * Searches for recipes by ingredient.
     *
     * @param ingredient The ingredient to search for
     * @return ResponseEntity containing matching recipes
     */
    @GetMapping("/search/ingredient")
    public ResponseEntity<List<Recipe>> searchRecipesByIngredient(@RequestParam String ingredient) {
        List<Recipe> recipes = recipeService.searchRecipesByIngredient(ingredient);
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    // Add advanced search endpoint
    /**
     * Advanced search for recipes with multiple criteria.
     *
     * <p>This endpoint searches for recipes based on a search term and maximum cooking time.</p>
     *
     * @param term The search term (optional)
     * @param maxCookingTime The maximum cooking time in minutes (optional)
     * @param page The page number (0-indexed)
     * @param size The page size
     * @return ResponseEntity containing matching recipes and pagination metadata
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchRecipes(
            @RequestParam(required = false) String term,
            @RequestParam(required = false) Integer maxCookingTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipePage = recipeService.searchRecipes(
                term != null ? term : "",
                maxCookingTime != null ? maxCookingTime : Integer.MAX_VALUE,
                pageable);

        return new ResponseEntity<>(createPaginationResponse(recipePage), HttpStatus.OK);
    }
}