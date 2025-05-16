package com.recipeapi.recipeapi.service;
import com.recipeapi.recipeapi.model.Recipe;
import com.recipeapi.recipeapi.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    private Recipe testRecipe;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testRecipe = new Recipe();
        testRecipe.setId("1");
        testRecipe.setTitle("Test Recipe");
        testRecipe.setIngredients(Arrays.asList("ingredient1", "ingredient2"));
        testRecipe.setInstructions("Test instructions");
        testRecipe.setCookingTime(30);
    }

    @Test
    public void testGetAllRecipes() {
        when(recipeRepository.findAll()).thenReturn(Arrays.asList(testRecipe));

        List<Recipe> result = recipeService.getAllRecipes();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    public void testGetRecipeById() {
        when(recipeRepository.findById("1")).thenReturn(Optional.of(testRecipe));

        Recipe result = recipeService.getRecipeById("1");

        assertNotNull(result);
        assertEquals("Test Recipe", result.getTitle());
        verify(recipeRepository, times(1)).findById("1");
    }

    @Test
    public void testCreateRecipe() {
        when(recipeRepository.save(any(Recipe.class))).thenReturn(testRecipe);

        Recipe result = recipeService.createRecipe(testRecipe);

        assertNotNull(result);
        assertEquals("Test Recipe", result.getTitle());
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }
}