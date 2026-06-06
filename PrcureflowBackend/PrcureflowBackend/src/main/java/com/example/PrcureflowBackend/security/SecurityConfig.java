package com.example.PrcureflowBackend.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
/*
 * SecurityConfig is the main security configuration class.
 *
 * It controls:
 * 1. Which APIs are public
 * 2. Which APIs require login/JWT
 * 3. Password encryption
 * 4. JWT filter registration
 * 5. CORS permission for React frontend
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /*
     * JwtAuthenticationFilter is our custom filter.
     *
     * It checks incoming requests for JWT tokens.
     * If a valid token is found, it authenticates the user.
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /*
     * Constructor injection.
     *
     * Spring automatically injects JwtAuthenticationFilter here.
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /*
     * PasswordEncoder bean.
     *
     * This is used in AuthService for:
     * 1. Encrypting password during registration
     * 2. Matching password during login
     *
     * BCryptPasswordEncoder stores passwords securely.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * SecurityFilterChain defines the security rules for the application.
     *
     * This is where we configure:
     * - CORS
     * - CSRF
     * - Session policy
     * - Public/private routes
     * - JWT filter
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            /*
             * Enable CORS configuration.
             *
             * This allows the React frontend running on port 5173
             * to call the Spring Boot backend running on port 8081.
             */
            .cors(Customizer.withDefaults())

            /*
             * Disable CSRF.
             *
             * CSRF protection is mainly needed for traditional web apps
             * that use server-side sessions and cookies.
             *
             * Our backend is a stateless REST API using JWT tokens,
             * so CSRF can be disabled.
             */
            .csrf(csrf -> csrf.disable())

            /*
             * Make the application stateless.
             *
             * Stateless means:
             * - Backend will not store user session.
             * - Every protected request must send JWT token.
             *
             * This is standard for REST APIs using JWT.
             */
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            /*
             * Authorization rules.
             *
             * /api/auth/** is public because users must be able to:
             * - register
             * - verify OTP
             * - login
             *
             * All other APIs require authentication.
             */
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )

            /*
             * Add our JWT filter before Spring Security's default
             * UsernamePasswordAuthenticationFilter.
             *
             * This ensures JWT token is checked before the request
             * reaches protected controllers.
             */
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    /*
     * CORS configuration.
     *
     * CORS stands for Cross-Origin Resource Sharing.
     *
     * Your React frontend runs on:
     * http://localhost:5173
     * or
     * http://127.0.0.1:5173
     *
     * Your Spring Boot backend runs on:
     * http://localhost:8081
     *
     * Since frontend and backend have different ports,
     * the browser treats them as different origins.
     *
     * This configuration allows React to call Spring Boot APIs.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        /*
         * Allowed frontend origins.
         *
         * Add both localhost and 127.0.0.1 because sometimes Vite opens
         * the app using 127.0.0.1 instead of localhost.
         */
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173"
        ));

        /*
         * Allowed HTTP methods.
         *
         * GET    → fetch data
         * POST   → create data
         * PUT    → update data
         * DELETE → delete data
         * OPTIONS → browser preflight request for CORS
         */
        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        /*
         * Allow all headers.
         *
         * This is needed because frontend sends headers like:
         * Content-Type: application/json
         * Authorization: Bearer <token>
         */
        configuration.setAllowedHeaders(List.of("*"));

        /*
         * Allow credentials.
         *
         * This allows browser requests to include credentials if needed.
         * For JWT header-based auth, this is safe for local development.
         */
        configuration.setAllowCredentials(true);

        /*
         * Register this CORS configuration for all backend routes.
         *
         * /** means every API endpoint.
         */
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}