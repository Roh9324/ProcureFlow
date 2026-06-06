package com.example.PrcureflowBackend.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/*
 * SecurityConfig is the main Spring Security configuration class.
 *
 * It controls:
 * 1. Which APIs are public
 * 2. Which APIs require JWT authentication
 * 3. Password encryption
 * 4. JWT filter registration
 * 5. CORS permission for React frontend
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /*
     * JwtAuthenticationFilter is our custom JWT filter.
     *
     * It checks protected requests for a JWT token.
     * If the token is valid, it sets the logged-in user
     * inside Spring SecurityContext.
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
     * This is used for:
     * 1. Encrypting passwords during registration
     * 2. Matching encrypted passwords during login
     *
     * BCrypt is a secure password hashing algorithm.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * SecurityFilterChain defines the complete security rules.
     *
     * This is where we configure:
     * - CORS
     * - CSRF
     * - Stateless session policy
     * - Public/private routes
     * - JWT filter
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            /*
             * Enable CORS.
             *
             * This uses the corsConfigurationSource() bean below.
             * It allows your React frontend to call backend APIs.
             */
            .cors(Customizer.withDefaults())

            /*
             * Disable CSRF.
             *
             * CSRF is mainly needed for session/cookie-based web apps.
             * This project uses JWT tokens and stateless REST APIs,
             * so CSRF is disabled.
             */
            .csrf(csrf -> csrf.disable())

            /*
             * Make backend stateless.
             *
             * Stateless means:
             * - No server-side session is stored.
             * - Every protected request must send JWT token.
             */
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            /*
             * Authorization rules.
             *
             * Important order:
             * 1. OPTIONS requests must be public for browser CORS preflight.
             * 2. /api/auth/** must be public because login/register happen before JWT exists.
             * 3. Everything else requires authentication.
             */
            .authorizeHttpRequests(auth -> auth

                /*
                 * Allow all OPTIONS requests.
                 *
                 * Browser sends OPTIONS request before POST/PUT/DELETE
                 * when frontend and backend are on different domains.
                 */
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                /*
                 * Public authentication endpoints.
                 *
                 * These endpoints should not require JWT:
                 * - POST /api/auth/register
                 * - POST /api/auth/login
                 * - POST /api/auth/verify-otp
                 */
                .requestMatchers("/api/auth/**").permitAll()

                /*
                 * All other endpoints require JWT authentication.
                 */
                .anyRequest().authenticated()
            )

            /*
             * Register JWT filter before Spring Security's default
             * UsernamePasswordAuthenticationFilter.
             *
             * This ensures JWT is checked before protected controller methods run.
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
     * Your frontend and backend run on different origins:
     *
     * Local frontend:
     * http://localhost:5173
     * http://127.0.0.1:5173
     *
     * Live frontend:
     * https://procure-flow-kvp9.vercel.app
     *
     * Live backend:
     * https://procureflow-backend-knk5.onrender.com
     *
     * The browser blocks cross-origin API calls unless backend explicitly
     * allows the frontend origin.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        /*
         * Allowed frontend origins.
         *
         * Only these frontend URLs can call the backend from browser.
         */
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "https://procure-flow-kvp9.vercel.app"
        ));

        /*
         * Allowed HTTP methods.
         *
         * GET     -> fetch data
         * POST    -> create/login/register
         * PUT     -> update workflow status
         * DELETE  -> delete data if needed
         * OPTIONS -> CORS preflight request
         */
        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        /*
         * Allowed headers.
         *
         * The frontend sends:
         * - Content-Type: application/json
         * - Authorization: Bearer <token>
         */
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept"
        ));

        /*
         * Exposed headers.
         *
         * This allows frontend JavaScript to read these response headers if needed.
         */
        configuration.setExposedHeaders(List.of(
                "Authorization"
        ));

        /*
         * Allow credentials.
         *
         * This is safe because allowed origins are specific,
         * not wildcard "*".
         */
        configuration.setAllowCredentials(true);

        /*
         * Cache preflight response for 1 hour.
         *
         * This reduces repeated OPTIONS requests from the browser.
         */
        configuration.setMaxAge(3600L);

        /*
         * Apply this CORS configuration to every backend route.
         */
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}