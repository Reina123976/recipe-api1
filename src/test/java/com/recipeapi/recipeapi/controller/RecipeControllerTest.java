package com.recipeapi.recipeapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipeapi.recipeapi.model.Recipe;
import com.recipeapi.recipeapi.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    private Recipe testRecipe;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        testRecipe = new Recipe();
        testRecipe.setId("1");
        testRecipe.setTitle("Test Recipe");
        testRecipe.setIngredients(Arrays.asList("ingredient1", "ingredient2"));
        testRecipe.setInstructions("Test instructions");
        testRecipe.setCookingTime(30);
    }

    @Test
    @WithMockUser
    public void testGetRecipeById() throws Exception {
        when(recipeService.getRecipeById("1")).thenReturn(testRecipe);

        mockMvc.perform(get("/api/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Test Recipe")));

        verify(recipeService, times(1)).getRecipeById("1");
    }

    @Test
    @WithMockUser
    public void testCreateRecipe() throws Exception {
        when(recipeService.createRecipe(any(Recipe.class))).thenReturn(testRecipe);

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRecipe)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Test Recipe")));

        verify(recipeService, times(1)).createRecipe(any(Recipe.class));
    }
}