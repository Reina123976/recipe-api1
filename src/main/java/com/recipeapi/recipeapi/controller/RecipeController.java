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

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // Create a new recipe
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@Valid @RequestBody Recipe recipe) {
        Recipe createdRecipe = recipeService.createRecipe(recipe);
        return new ResponseEntity<>(createdRecipe, HttpStatus.CREATED);
    }

    // Helper method to create response map from Page object (to avoid code duplication)
    private Map<String, Object> createPaginationResponse(Page<Recipe> recipePage) {
        Map<String, Object> response = new HashMap<>();
        response.put("recipes", recipePage.getContent());
        response.put("currentPage", recipePage.getNumber());
        response.put("totalItems", recipePage.getTotalElements());
        response.put("totalPages", recipePage.getTotalPages());
        return response;
    }

    // Get all recipes with pagination and sorting
    @GetMapping
    public ResponseEntity<?> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
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
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable String id) {
        Recipe recipe = recipeService.getRecipeById(id);
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }

    // Update a recipe
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable String id, @Valid @RequestBody Recipe recipe) {
        Recipe updatedRecipe = recipeService.updateRecipe(id, recipe);
        return new ResponseEntity<>(updatedRecipe, HttpStatus.OK);
    }

    // Delete a recipe
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteRecipe(@PathVariable String id) {
        recipeService.deleteRecipe(id);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get recipes by category with pagination
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
    @GetMapping("/search/title")
    public ResponseEntity<List<Recipe>> searchRecipesByTitle(@RequestParam String title) {
        List<Recipe> recipes = recipeService.searchRecipesByTitle(title);
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    // Add search endpoint for ingredients
    @GetMapping("/search/ingredient")
    public ResponseEntity<List<Recipe>> searchRecipesByIngredient(@RequestParam String ingredient) {
        List<Recipe> recipes = recipeService.searchRecipesByIngredient(ingredient);
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    // Add advanced search endpoint
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