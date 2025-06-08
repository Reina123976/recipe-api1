package com.recipeapi.recipeapi.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Bean configuration class for the Recipe API application.
 *
 * <p>This class provides bean definitions for various components used throughout the application.</p>
 *
 * @author Your Name
 * @version 1.0
 */
@Configuration
public class BeanConfig {

    /**
     * Creates and configures a password encoder bean.
     *
     * <p>This bean is used for securely hashing user passwords before storage
     * and for password verification during authentication.</p>
     *
     * @return A BCryptPasswordEncoder instance
     */
    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}