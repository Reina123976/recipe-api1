package com.recipeapi.Client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class RecipeApiClient {

    private static final String API_BASE_URL = "http://localhost:8081/api";
    private static final Scanner scanner = new Scanner(System.in);
    private static String token = null;
    private static String username = null;

    public static void main(String[] args) {
        System.out.println("Welcome to Recipe Management System");

        boolean exit = false;
        while (!exit) {
            if (token == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }

            int choice = getUserChoice();

            if (token == null) {
                switch (choice) {
                    case 1:
                        login();
                        break;
                    case 2:
                        register();
                        break;
                    case 3:
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                switch (choice) {
                    case 1:
                        listAllRecipes();
                        break;
                    case 2:
                        addRecipe();
                        break;
                    case 3:
                        updateRecipe();
                        break;
                    case 4:
                        deleteRecipe();
                        break;
                    case 5:
                        searchRecipes();
                        break;
                    case 6:
                        logout();
                        break;
                    case 7:
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }

        System.out.println("Thank you for using Recipe Management System. Goodbye!");
        scanner.close();
    }

    private static void showAuthMenu() {
        System.out.println("\n=== Authentication Menu ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void showMainMenu() {
        System.out.println("\n=== Recipe Management Menu ===");
        System.out.println("Logged in as: " + username);
        System.out.println("1. List All Recipes");
        System.out.println("2. Add Recipe");
        System.out.println("3. Update Recipe");
        System.out.println("4. Delete Recipe");
        System.out.println("5. Search Recipes");
        System.out.println("6. Logout");
        System.out.println("7. Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void login() {
        System.out.println("\n=== Login ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            // Create login JSON payload
            JSONObject loginData = new JSONObject();
            loginData.put("username", username);
            loginData.put("password", password);

            // Send login request
            HttpURLConnection connection = createConnection(API_BASE_URL + "/auth/login", "POST");

            // Set request body
            connection.setDoOutput(true);
            connection.getOutputStream().write(loginData.toString().getBytes());

            // Get response
            int responseCode = connection.getResponseCode();
            String responseBody = readResponse(connection);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                JSONObject jsonResponse = new JSONObject(responseBody);

                // Save token and username
                token = jsonResponse.getString("token");
                RecipeApiClient.username = jsonResponse.getString("username");

                System.out.println("Login successful!");
            } else {
                System.out.println("Login failed: " + connection.getResponseMessage());
                System.out.println("Response: " + responseBody);
            }

            connection.disconnect();

        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void register() {
        System.out.println("\n=== Register ===");
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            // Create register JSON payload
            JSONObject registerData = new JSONObject();
            registerData.put("name", name);
            registerData.put("username", username);
            registerData.put("email", email);
            registerData.put("password", password);

            // Default role
            JSONArray roles = new JSONArray();
            roles.put("USER");
            registerData.put("roles", roles);

            // Send register request
            HttpURLConnection connection = createConnection(API_BASE_URL + "/auth/register", "POST");

            // Set request body
            connection.setDoOutput(true);
            connection.getOutputStream().write(registerData.toString().getBytes());

            // Get response
            int responseCode = connection.getResponseCode();
            String responseBody = readResponse(connection);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Registration successful! Please login.");
            } else {
                System.out.println("Registration failed: " + connection.getResponseMessage());
                System.out.println("Response: " + responseBody);
            }

            connection.disconnect();

        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void logout() {
        token = null;
        username = null;
        System.out.println("Logged out successfully.");
    }

    private static void listAllRecipes() {
        System.out.println("\n=== All Recipes ===");

        try {
            // Send request to get all recipes with large size parameter to get all at once
            HttpURLConnection connection = createConnection(
                    API_BASE_URL + "/recipes?size=1000", "GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);

            // Get response
            int responseCode = connection.getResponseCode();
            String responseBody = readResponse(connection);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                JSONArray recipes;

                try {
                    // Try parsing directly as array first
                    recipes = new JSONArray(responseBody);
                } catch (Exception e) {
                    // If not an array, try extracting from paginated response
                    try {
                        JSONObject paginatedResponse = new JSONObject(responseBody);
                        recipes = paginatedResponse.getJSONArray("recipes");
                    } catch (Exception ex) {
                        System.out.println("Failed to parse recipe data: " + ex.getMessage());
                        System.out.println("Response Body: " + responseBody);
                        connection.disconnect();
                        return;
                    }
                }

                if (recipes.length() == 0) {
                    System.out.println("No recipes found.");
                } else {
                    for (int i = 0; i < recipes.length(); i++) {
                        JSONObject recipe = recipes.getJSONObject(i);
                        displayRecipe(recipe);
                    }
                }
            } else {
                System.out.println("Failed to fetch recipes: Status Code " + responseCode);
                System.out.println("Response Message: " + connection.getResponseMessage());
                System.out.println("Response Body: " + responseBody);
            }

            connection.disconnect();

        } catch (Exception e) {
            System.out.println("Error fetching recipes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void addRecipe() {
        System.out.println("\n=== Add Recipe ===");

        // Check if token is valid
        if (token == null || token.isEmpty()) {
            System.out.println("You need to log in first!");
            login();
            if (token == null) {
                System.out.println("Login failed. Cannot add recipe.");
                return;
            }
        }

        try {
            // Create a complete recipe JSON object manually
            JSONObject recipeData = new JSONObject();

            // Get title
            System.out.print("Title: ");
            String title = scanner.nextLine();
            if (title.isEmpty()) {
                System.out.println("Title is required!");
                return;
            }
            recipeData.put("title", title);

            // Get category
            System.out.print("Category: ");
            String category = scanner.nextLine();
            if (!category.isEmpty()) {
                recipeData.put("category", category);
            }

            // Get cooking time
            System.out.print("Cooking Time (minutes): ");
            String cookingTimeStr = scanner.nextLine();
            int cookingTime = 30; // Default
            if (!cookingTimeStr.isEmpty()) {
                try {
                    cookingTime = Integer.parseInt(cookingTimeStr);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid cooking time. Using default value of 30 minutes.");
                }
            }
            recipeData.put("cookingTime", cookingTime);

            // Get ingredients
            List<String> ingredients = new ArrayList<>();
            System.out.println("Enter ingredients (leave empty to finish):");
            int ingredientNum = 1;
            while (true) {
                System.out.print("Ingredient " + ingredientNum + ": ");
                String ingredient = scanner.nextLine();
                if (ingredient.isEmpty()) {
                    break;
                }
                ingredients.add(ingredient);
                ingredientNum++;
            }

            if (ingredients.isEmpty()) {
                System.out.println("At least one ingredient is required!");
                return;
            }

            JSONArray ingredientsArray = new JSONArray();
            for (String ingredient : ingredients) {
                ingredientsArray.put(ingredient);
            }
            recipeData.put("ingredients", ingredientsArray);

            // Get instructions
            System.out.print("Instructions: ");
            String instructions = scanner.nextLine();
            if (instructions.isEmpty()) {
                System.out.println("Instructions are required!");
                return;
            }
            recipeData.put("instructions", instructions);

            // Add createdBy field explicitly (set to current username)
            recipeData.put("createdBy", username);

            // Print the complete recipe data for verification
            System.out.println("\nRecipe Data:");
            System.out.println("Title: " + recipeData.getString("title"));
            System.out.println("Category: " + (recipeData.has("category") ? recipeData.getString("category") : "None"));
            System.out.println("Cooking Time: " + recipeData.getInt("cookingTime") + " minutes");
            System.out.println("Ingredients: " + ingredientsArray.toString());
            System.out.println("Instructions: " + recipeData.getString("instructions"));
            System.out.println("Created By: " + recipeData.getString("createdBy"));

            System.out.print("\nDo you want to submit this recipe? (y/n): ");
            String confirm = scanner.nextLine();
            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("Recipe submission cancelled.");
                return;
            }

            // Send request to create recipe
            HttpURLConnection connection = createConnection(API_BASE_URL + "/recipes", "POST");
            System.out.println("Using auth token: " + token.substring(0, Math.min(10, token.length())) + "...");
            connection.setRequestProperty("Authorization", "Bearer " + token);

            // Set request body
            connection.setDoOutput(true);
            String jsonData = recipeData.toString();
            System.out.println("Sending recipe data: " + jsonData);
            connection.getOutputStream().write(jsonData.getBytes());
            connection.getOutputStream().flush();

            // Get response code first
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Then get response body safely
            String responseBody = readResponse(connection);

            if (responseCode >= 200 && responseCode < 300) {
                System.out.println("Recipe added successfully!");
                if (responseBody != null && !responseBody.isEmpty()) {
                    try {
                        JSONObject addedRecipe = new JSONObject(responseBody);
                        System.out.println("Added Recipe Details:");
                        displayRecipe(addedRecipe);
                    } catch (Exception e) {
                        System.out.println("Received response: " + responseBody);
                    }
                }
            } else {
                System.out.println("Failed to add recipe: Status Code " + responseCode);
                System.out.println("Response Message: " + connection.getResponseMessage());
                System.out.println("Response Body: " + responseBody);
            }

            connection.disconnect();

        } catch (Exception e) {
            System.out.println("Error adding recipe: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void updateRecipe() {
        System.out.println("\n=== Update Recipe ===");
        System.out.print("Enter recipe ID to update: ");
        String recipeId = scanner.nextLine();

        try {
            // Check if recipe exists
            HttpURLConnection getConnection = createConnection(API_BASE_URL + "/recipes/" + recipeId, "GET");
            getConnection.setRequestProperty("Authorization", "Bearer " + token);

            int getResponseCode = getConnection.getResponseCode();
            String getResponseBody = readResponse(getConnection);

            if (getResponseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Recipe not found or you don't have permission to update it.");
                System.out.println("Status Code: " + getResponseCode);
                System.out.println("Response: " + getResponseBody);
                getConnection.disconnect();
                return;
            }

            JSONObject existingRecipe = new JSONObject(getResponseBody);
            getConnection.disconnect();

            // Gather updated recipe information
            JSONObject recipeData = getRecipeDataFromUser(existingRecipe);

            if (recipeData == null) {
                System.out.println("Recipe update cancelled.");
                return;
            }

            // Send request to update recipe
            HttpURLConnection updateConnection = createConnection(API_BASE_URL + "/recipes/" + recipeId, "PUT");
            updateConnection.setRequestProperty("Authorization", "Bearer " + token);

            // Set request body
            updateConnection.setDoOutput(true);
            String jsonData = recipeData.toString();
            System.out.println("Sending update data: " + jsonData);
            updateConnection.getOutputStream().write(jsonData.getBytes());

            // Get response
            int updateResponseCode = updateConnection.getResponseCode();
            String updateResponseBody = readResponse(updateConnection);

            if (updateResponseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Recipe updated successfully!");
                if (!updateResponseBody.isEmpty()) {
                    try {
                        JSONObject updatedRecipe = new JSONObject(updateResponseBody);
                        System.out.println("Updated Recipe Details:");
                        displayRecipe(updatedRecipe);
                    } catch (Exception e) {
                        System.out.println("Received response: " + updateResponseBody);
                    }
                }
            } else {
                System.out.println("Failed to update recipe: Status Code " + updateResponseCode);
                System.out.println("Response Message: " + updateConnection.getResponseMessage());
                System.out.println("Response Body: " + updateResponseBody);
            }

            updateConnection.disconnect();

        } catch (Exception e) {
            System.out.println("Error updating recipe: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void deleteRecipe() {
        System.out.println("\n=== Delete Recipe ===");
        System.out.print("Enter recipe ID to delete: ");
        String recipeId = scanner.nextLine();

        try {
            // Send request to delete recipe
            HttpURLConnection connection = createConnection(API_BASE_URL + "/recipes/" + recipeId, "DELETE");
            connection.setRequestProperty("Authorization", "Bearer " + token);

            // Get response
            int responseCode = connection.getResponseCode();
            String responseBody = readResponse(connection);

            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Recipe deleted successfully!");
            } else {
                System.out.println("Failed to delete recipe: Status Code " + responseCode);
                System.out.println("Response Message: " + connection.getResponseMessage());
                System.out.println("Response Body: " + responseBody);
            }

            connection.disconnect();

        } catch (Exception e) {
            System.out.println("Error deleting recipe: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void searchRecipes() {
        System.out.println("\n=== Search Recipes ===");
        System.out.print("Enter search term: ");
        String searchTerm = scanner.nextLine();

        try {
            // Send request to search recipes
            HttpURLConnection connection = createConnection(
                    API_BASE_URL + "/recipes/search/title?title=" + searchTerm, "GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);

            // Get response
            int responseCode = connection.getResponseCode();
            String responseBody = readResponse(connection);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                JSONArray recipes;

                try {
                    // Try parsing directly as array first
                    recipes = new JSONArray(responseBody);
                } catch (Exception e) {
                    // If not an array, try extracting from paginated response
                    try {
                        JSONObject paginatedResponse = new JSONObject(responseBody);
                        recipes = paginatedResponse.getJSONArray("recipes");
                    } catch (Exception ex) {
                        System.out.println("Failed to parse recipe data: " + ex.getMessage());
                        System.out.println("Response Body: " + responseBody);
                        connection.disconnect();
                        return;
                    }
                }

                if (recipes.length() == 0) {
                    System.out.println("No recipes found matching your search.");
                } else {
                    System.out.println("Found " + recipes.length() + " recipes:");
                    for (int i = 0; i < recipes.length(); i++) {
                        JSONObject recipe = recipes.getJSONObject(i);
                        displayRecipe(recipe);
                    }
                }
            } else {
                System.out.println("Failed to search recipes: Status Code " + responseCode);
                System.out.println("Response Message: " + connection.getResponseMessage());
                System.out.println("Response Body: " + responseBody);
            }

            connection.disconnect();

        } catch (Exception e) {
            System.out.println("Error searching recipes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static JSONObject getRecipeDataFromUser(JSONObject existingRecipe) {
        JSONObject recipeData = new JSONObject();
        boolean missingRequiredFields = false;

        // Title (required field)
        System.out.print("Title: ");
        String title = scanner.nextLine();
        if (!title.isEmpty()) {
            recipeData.put("title", title);
        } else if (existingRecipe != null && existingRecipe.has("title")) {
            recipeData.put("title", existingRecipe.getString("title"));
        } else {
            System.out.println("Title is required!");
            missingRequiredFields = true;
        }

        // Category
        System.out.print("Category: ");
        String category = scanner.nextLine();
        if (!category.isEmpty()) {
            recipeData.put("category", category);
        } else if (existingRecipe != null && existingRecipe.has("category")) {
            recipeData.put("category", existingRecipe.getString("category"));
        }

        // Cooking Time
        System.out.print("Cooking Time (minutes): ");
        String cookingTimeStr = scanner.nextLine();
        if (!cookingTimeStr.isEmpty()) {
            try {
                int cookingTime = Integer.parseInt(cookingTimeStr);
                recipeData.put("cookingTime", cookingTime);
            } catch (NumberFormatException e) {
                System.out.println("Invalid cooking time. Using default or existing value.");
                if (existingRecipe != null && existingRecipe.has("cookingTime")) {
                    recipeData.put("cookingTime", existingRecipe.getInt("cookingTime"));
                } else {
                    recipeData.put("cookingTime", 30); // Default value
                }
            }
        } else if (existingRecipe != null && existingRecipe.has("cookingTime")) {
            recipeData.put("cookingTime", existingRecipe.getInt("cookingTime"));
        } else {
            recipeData.put("cookingTime", 30); // Default value
        }

        // Get ingredients (required field)
        List<String> ingredients = new ArrayList<>();
        boolean addMoreIngredients = true;

        if (existingRecipe != null && existingRecipe.has("ingredients")) {
            JSONArray existingIngredients = existingRecipe.getJSONArray("ingredients");
            System.out.println("Current ingredients:");
            for (int i = 0; i < existingIngredients.length(); i++) {
                System.out.println((i + 1) + ". " + existingIngredients.getString(i));
            }

            System.out.print("Do you want to replace ingredients? (y/n): ");
            String replaceIngredientsChoice = scanner.nextLine();

            if (!replaceIngredientsChoice.equalsIgnoreCase("y")) {
                // Keep existing ingredients
                for (int i = 0; i < existingIngredients.length(); i++) {
                    ingredients.add(existingIngredients.getString(i));
                }
                addMoreIngredients = false;
            }
        }

        if (addMoreIngredients) {
            System.out.println("Enter ingredients (leave empty to finish):");
            int ingredientNum = 1;
            while (true) {
                System.out.print("Ingredient " + ingredientNum + ": ");
                String ingredient = scanner.nextLine();
                if (ingredient.isEmpty()) {
                    break;
                }
                ingredients.add(ingredient);
                ingredientNum++;
            }
        }

        if (ingredients.isEmpty() && existingRecipe == null) {
            System.out.println("At least one ingredient is required!");
            missingRequiredFields = true;
        }

        JSONArray ingredientsArray = new JSONArray();
        for (String ingredient : ingredients) {
            ingredientsArray.put(ingredient);
        }
        recipeData.put("ingredients", ingredientsArray);

        // Instructions (required field)
        System.out.print("Instructions: ");
        String instructions = scanner.nextLine();

        // Remove any existing quotes at the beginning and end if present
        if (instructions.startsWith("\"")) {
            instructions = instructions.substring(1);
        }
        if (instructions.endsWith("\"")) {
            instructions = instructions.substring(0, instructions.length() - 1);
        }

        if (!instructions.isEmpty()) {
            recipeData.put("instructions", instructions); // Don't add extra quotes
        } else if (existingRecipe != null && existingRecipe.has("instructions")) {
            recipeData.put("instructions", existingRecipe.getString("instructions"));
        } else {
            System.out.println("Instructions are required!");
            missingRequiredFields = true;
        }

        // Preserve createdBy field if it exists in the existing recipe
        if (existingRecipe != null && existingRecipe.has("createdBy")) {
            recipeData.put("createdBy", existingRecipe.getString("createdBy"));
        } else {
            // Set createdBy to current username for new recipes
            recipeData.put("createdBy", username);
        }

        // Final check for required fields
        if (!recipeData.has("title") || !recipeData.has("ingredients") || !recipeData.has("instructions")) {
            System.out.println("ERROR: Missing required fields in recipe data!");
            System.out.println("Available fields: " + recipeData.toString());
            missingRequiredFields = true;
        }

        if (missingRequiredFields) {
            System.out.println("Please fill all required fields!");
            return null;
        }

        return recipeData;
    }

    private static void displayRecipe(JSONObject recipe) {
        System.out.println("\n-----------------------------");
        System.out.println("ID: " + recipe.getString("id"));
        System.out.println("Title: " + recipe.getString("title"));

        if (recipe.has("category") && !recipe.isNull("category")) {
            System.out.println("Category: " + recipe.getString("category"));
        }

        if (recipe.has("cookingTime") && !recipe.isNull("cookingTime")) {
            System.out.println("Cooking Time: " + recipe.getInt("cookingTime") + " minutes");
        }

        System.out.println("Ingredients:");
        JSONArray ingredients = recipe.getJSONArray("ingredients");
        for (int i = 0; i < ingredients.length(); i++) {
            System.out.println("- " + ingredients.getString(i));
        }

        System.out.println("Instructions: " + recipe.getString("instructions"));

        // Add null check for createdBy field
        if (recipe.has("createdBy") && !recipe.isNull("createdBy")) {
            System.out.println("Created By: " + recipe.getString("createdBy"));
        } else {
            System.out.println("Created By: Unknown");
        }

        System.out.println("-----------------------------");
    }

    private static HttpURLConnection createConnection(String urlStr, String method) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }

    private static String readResponse(HttpURLConnection connection) throws IOException {
        // Check if either input or error stream is available
        if (connection == null) {
            return "No connection available";
        }

        BufferedReader reader = null;
        try {
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() <= 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                if (connection.getErrorStream() != null) {
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                } else {
                    return "No error stream available";
                }
            }

            if (reader == null) {
                return "Could not create reader";
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (Exception e) {
            return "Error reading response: " + e.getMessage();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Ignore close error
                }
            }
        }
    }
}