
/**
 * Security package for the Recipe API application.
 *
 * <p>This package contains security-related classes, including JWT authentication,
 * user details service, and security utilities.</p>
 *
 * <p>Key components in this package include:</p>
 * <ul>
 *   <li>{@link com.recipeapi.recipeapi.security.JwtAuthenticationFilter} - Filters requests to validate JWT tokens</li>
 *   <li>{@link com.recipeapi.recipeapi.security.JwtService} - Handles JWT token generation and validation</li>
 *   <li>{@link com.recipeapi.recipeapi.security.UserService} - Implements UserDetailsService for authentication</li>
 * </ul>
 *
 * <p>This package implements a stateless authentication mechanism using JWT tokens,
 * allowing secure access to protected resources without requiring session state.</p>
 *
 * @author Your Name
 * @version 1.0
 */
package com.recipeapi.recipeapi.security;