package com.recipeapi.recipeapi.security;

import com.recipeapi.recipeapi.model.User;
import com.recipeapi.recipeapi.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for user operations.
 *
 * <p>This service provides user-related functionality, including:
 * <ul>
 *   <li>User registration and authentication</li>
 *   <li>User management (retrieving, deleting)</li>
 *   <li>Implementation of Spring Security's UserDetailsService</li>
 * </ul>
 * </p>
 *
 * @author Reina
 * @version 1.0
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new UserService with the necessary dependencies.
     *
     * @param userRepository Repository for user operations
     * @param passwordEncoder Encoder for password hashing
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Loads a user by username.
     *
     * <p>This method is required by the UserDetailsService interface
     * and is used by Spring Security for authentication.</p>
     *
     * @param username The username to load
     * @return The user details
     * @throws UsernameNotFoundException If no user is found with the username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    /**
     * Registers a new user.
     *
     * <p>This method:
     * <ul>
     *   <li>Validates that the username and email are not already in use</li>
     *   <li>Encodes the password</li>
     *   <li>Creates and saves a new user</li>
     * </ul>
     * </p>
     *
     * @param username The username for the new user
     * @param password The password for the new user
     * @param email The email for the new user
     * @param roles The roles to assign to the new user
     * @return The created user
     * @throws IllegalArgumentException If the username or email is already in use
     */
    public User registerUser(String username, String password, String email, List<String> roles) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRoles(roles != null ? roles : new ArrayList<>());

        return userRepository.save(user);
    }

    /**
     * Retrieves all users.
     *
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id The ID of the user to retrieve
     * @return The user
     * @throws UsernameNotFoundException If no user is found with the ID
     */
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    /**
     * Deletes a user by ID.
     *
     * @param id The ID of the user to delete
     * @throws UsernameNotFoundException If no user is found with the ID
     */
    public void deleteUser(String id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}