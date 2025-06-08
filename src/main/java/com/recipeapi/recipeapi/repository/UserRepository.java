package com.recipeapi.recipeapi.repository;
import com.recipeapi.recipeapi.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/**
 * Repository interface for User entities.
 *
 * <p>This interface provides methods for CRUD operations on users,
 * as well as custom queries for finding users by username and email.</p>
 *
 * @author ReinaKazan
 * @version 1.0
 */

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    /**
     * Finds a user by username.
     *
     * @param username The username to search for
     * @return An Optional containing the user if found, or empty if not found
     */
    Optional<User> findByUsername(String username);
    /**
     * Checks if a user with the given username exists.
     *
     * @param username The username to check
     * @return true if a user with the username exists, false otherwise
     */
    Boolean existsByUsername(String username);
    /**
     * Checks if a user with the given email exists.
     *
     * @param email The email to check
     * @return true if a user with the email exists, false otherwise
     */

    Boolean existsByEmail(String email);
}