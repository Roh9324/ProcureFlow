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
 * 1. Public APIs
 * 2. Protected APIs
 * 3. JWT authentication
 * 4. Password encryption
 * 5. CORS settings for React frontend
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /*
     * Constructor injection.
     *
     * Spring injects our custom JWT filter.
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /*
     * PasswordEncoder bean.
     *
     * Used for:
     * 1. Encrypting password during registration
     * 2. Matching password during login
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * Main security filter chain.
     *
     * This defines:
     * - CORS
     * - CSRF
     * - Stateless JWT session
     * - Public/private routes
     * - JWT filter registration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            /*
             * Enable CORS using corsConfigurationSource().
             */
            .cors(Customizer.withDefaults())

            /*
             * Disable CSRF because this is a stateless REST API using JWT.
             */
            .csrf(csrf -> csrf.disable())

            /*
             * Disable server-side sessions.
             *
             * Every protected request must send JWT token.
             */
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            /*
             * Authorization rules.
             *
             * Order is important:
             * 1. OPTIONS requests are allowed for browser preflight.
             * 2. /api/auth/** is public for register/login/OTP.
             * 3. All other APIs require authentication.
             */
            .authorizeHttpRequests(auth -> auth

                /*
                 * Allow browser CORS preflight requests.
                 */
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                /*
                 * Public authentication APIs.
                 *
                 * These should work without JWT:
                 * - register
                 * - login
                 * - OTP verification
                 */
                .requestMatchers("/api/auth/**").permitAll()

                /*
                 * Every other API requires JWT.
                 */
                .anyRequest().authenticated()
            )

            /*
             * Register custom JWT filter before Spring Security's
             * UsernamePasswordAuthenticationFilter.
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
     * Allows local frontend and deployed Vercel frontend
     * to call the backend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        /*
         * Allowed frontend URLs.
         *
         * Local:
         * http://localhost:5173
         * http://127.0.0.1:5173
         *
         * Deployed frontend:
         * https://procure-flow-kvp9.vercel.app
         */
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "https://procure-flow-kvp9.vercel.app"
        ));

        /*
         * Allowed HTTP methods.
         */
        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        /*
         * Allowed request headers from frontend.
         */
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept"
        ));

        /*
         * Headers frontend is allowed to read from response.
         */
        configuration.setExposedHeaders(List.of(
                "Authorization"
        ));

        /*
         * Allow credentials.
         *
         * Safe because we are using specific origins,
         * not wildcard "*".
         */
        configuration.setAllowCredentials(true);

        /*
         * Cache preflight response for 1 hour.
         */
        configuration.setMaxAge(3600L);

        /*
         * Apply CORS to all backend routes.
         */
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}