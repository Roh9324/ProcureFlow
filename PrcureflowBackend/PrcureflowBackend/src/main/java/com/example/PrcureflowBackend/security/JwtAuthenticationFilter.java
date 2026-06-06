package com.example.PrcureflowBackend.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.PrcureflowBackend.user.User;
import com.example.PrcureflowBackend.user.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * JwtAuthenticationFilter runs once for every incoming HTTP request.
 *
 * Main responsibility:
 * - Read JWT token from Authorization header
 * - Validate the token
 * - Extract logged-in user's email
 * - Load user role from database
 * - Set authentication in Spring Security context
 *
 * After this filter sets authentication, @PreAuthorize can check roles like:
 * hasRole('EMPLOYEE')
 * hasRole('HR_MANAGER')
 * hasRole('ADMIN')
 * hasRole('FINAL_APPROVER')
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    /*
     * Constructor injection.
     *
     * Spring automatically injects JwtService and UserRepository.
     */
    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    /*
     * This method runs for every request.
     *
     * Example requests:
     * - POST /api/auth/login
     * - POST /api/auth/register
     * - GET /api/users/me
     * - POST /api/asset-requests
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        /*
         * Get the request path.
         *
         * Example:
         * /api/auth/login
         * /api/asset-requests/my
         */
        String path = request.getServletPath();

        /*
         * Allow authentication APIs without JWT.
         *
         * These endpoints are public because the user does not have a token yet.
         *
         * Without this block, login/register can fail with 403
         * because the JWT filter may try to validate a missing token.
         */
        if (path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Allow CORS preflight requests.
         *
         * Browsers send OPTIONS requests before POST/PUT/DELETE
         * when frontend and backend are on different domains.
         *
         * These requests do not contain JWT tokens.
         */
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Read Authorization header.
         *
         * Expected format:
         * Authorization: Bearer <jwt-token>
         */
        String authHeader = request.getHeader("Authorization");

        /*
         * If Authorization header is missing or does not start with Bearer,
         * do not authenticate here.
         *
         * Protected APIs will still be blocked later by Spring Security.
         */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Remove "Bearer " prefix and keep only the token.
         */
        String token = authHeader.substring(7);

        try {
            /*
             * Extract email from JWT token.
             *
             * This should match the email used during login.
             */
            String userEmail = jwtService.extractEmail(token);

            /*
             * If email exists and no authentication is already set,
             * validate token and authenticate the user.
             */
            if (
                    userEmail != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null
            ) {
                /*
                 * Load user from database.
                 *
                 * We need this to get the user's current role.
                 */
                User user = userRepository
                        .findByEmail(userEmail)
                        .orElse(null);

                /*
                 * If user does not exist anymore, continue without authentication.
                 *
                 * Spring Security will block protected endpoints.
                 */
                if (user == null) {
                    filterChain.doFilter(request, response);
                    return;
                }

                /*
                 * Validate JWT token.
                 *
                 * This usually checks:
                 * - token email matches user email
                 * - token is not expired
                 * - token signature is valid
                 */
                boolean tokenValid = jwtService.isTokenValid(token, userEmail);

                if (tokenValid) {

                    /*
                     * Convert database role into Spring Security role.
                     *
                     * Spring Security expects role authority format:
                     * ROLE_EMPLOYEE
                     * ROLE_HR_MANAGER
                     * ROLE_ADMIN
                     * ROLE_FINAL_APPROVER
                     */
                    String roleName = user.getRole().getName().name();

                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + roleName)
                    );

                    /*
                     * Create Spring Security authentication object.
                     *
                     * Principal:
                     * - userEmail
                     *
                     * Credentials:
                     * - null because password is not needed here
                     *
                     * Authorities:
                     * - user's role
                     */
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userEmail,
                                    null,
                                    authorities
                            );

                    /*
                     * Attach request details such as remote address/session info.
                     */
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    /*
                     * Store authentication in SecurityContext.
                     *
                     * After this, controllers and @PreAuthorize can identify
                     * the logged-in user and their role.
                     */
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authenticationToken);
                }
            }

        } catch (Exception ex) {
            /*
             * If token is invalid, expired, malformed, or cannot be parsed,
             * clear security context.
             *
             * Do not throw error here.
             * Spring Security will reject protected endpoints naturally.
             */
            SecurityContextHolder.clearContext();
        }

        /*
         * Continue request processing.
         */
        filterChain.doFilter(request, response);
    }
}