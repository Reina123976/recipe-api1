package com.recipeapi.recipeapi.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

/**
 * Model class representing a recipe in the system.
 *
 * <p>This class maps to documents in the "recipes" collection in MongoDB.</p>
 *
 */
@Document(collection = "recipes")
public class Recipe {
    @Id
    private String id;
    private String title;
    private List<String> ingredients;
    private String instructions;
    private Integer cookingTime;
    private String category;
    private String createdBy; // Added field

    // Default constructor required by MongoDB
    public Recipe() {
    }

    /**
     * Creates a new Recipe with the specified details.
     *
     * @param title The recipe title
     * @param ingredients List of ingredients
     * @param instructions Cooking instructions
     * @param cookingTime Cooking time in minutes
     * @param category Recipe category
     * @param createdBy Username of the creator
     */
    public Recipe(String title, List<String> ingredients, String instructions,
                  Integer cookingTime, String category, String createdBy) {
        this.title = title;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.cookingTime = cookingTime;
        this.category = category;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Integer getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(Integer cookingTime) {
        this.cookingTime = cookingTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Returns a string representation of the recipe.
     *
     * @return A string containing all recipe details
     */
    @Override
    public String toString() {
        return "Recipe{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", ingredients=" + ingredients +
                ", instructions='" + instructions + '\'' +
                ", cookingTime=" + cookingTime +
                ", category='" + category + '\'' +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}