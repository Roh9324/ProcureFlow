package com.example.PrcureflowBackend.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.PrcureflowBackend.user.User;
import com.example.PrcureflowBackend.user.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * JwtAuthenticationFilter runs before protected API requests.
 *
 * Its job:
 * 1. Read JWT token from Authorization header
 * 2. Validate token
 * 3. Find user from database
 * 4. Tell Spring Security that this request is authenticated
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    /*
     * Constructor injection.
     *
     * Spring automatically provides:
     * - JwtService
     * - UserRepository
     */
    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    /*
     * This method runs for every HTTP request.
     *
     * Example:
     * GET /api/users/me
     * POST /api/asset-requests
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        /*
         * Get Authorization header from request.
         *
         * Expected format:
         * Authorization: Bearer jwt_token_here
         */
        String authHeader = request.getHeader("Authorization");

        /*
         * If Authorization header is missing or does not start with Bearer,
         * continue request without authentication.
         *
         * Public APIs like /api/auth/login do not need token.
         */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Remove "Bearer " from header and keep only token.
         *
         * Example:
         * Bearer abc.xyz.token
         * becomes:
         * abc.xyz.token
         */
        String token = authHeader.substring(7);

        /*
         * Extract email from token.
         * Email was stored as JWT subject when token was created.
         */
        String email = jwtService.extractEmail(token);

        /*
         * Authenticate only if:
         * 1. email exists in token
         * 2. Spring Security context does not already have authentication
         */
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            /*
             * Find user in database using email from token.
             */
            User user = userRepository
                    .findByEmail(email)
                    .orElse(null);

            /*
             * If user exists and token is valid,
             * create authentication object for Spring Security.
             */
            if (user != null && jwtService.isTokenValid(token)) {

                /*
                 * Spring Security roles usually start with "ROLE_".
                 *
                 * Example:
                 * EMPLOYEE becomes ROLE_EMPLOYEE
                 * HR_MANAGER becomes ROLE_HR_MANAGER
                 */
                String roleName = user.getRole() != null
                        ? "ROLE_" + user.getRole().getName().name()
                        : "ROLE_EMPLOYEE";

                /*
                 * Create Spring Security authentication object.
                 *
                 * Principal = user email
                 * Credentials = null because password is not needed here
                 * Authorities = user role
                 */
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user.getEmail(),
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority(roleName))
                        );

                /*
                 * Store authentication in SecurityContext.
                 *
                 * After this, Spring knows this request is authenticated.
                 */
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        /*
         * Continue request to controller.
         */
        filterChain.doFilter(request, response);
    }
}