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
 * JwtAuthenticationFilter runs once for incoming HTTP requests.
 *
 * Main responsibility:
 * 1. Read JWT token from Authorization header
 * 2. Validate JWT token
 * 3. Extract logged-in user's email
 * 4. Load user role from database
 * 5. Set authentication in Spring Security context
 *
 * Important:
 * This filter should NOT run for public authentication APIs:
 * - /api/auth/register
 * - /api/auth/login
 * - /api/auth/verify-otp
 *
 * If this filter runs on register/login, those APIs may return 403
 * because the user does not have a JWT token yet.
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
     * shouldNotFilter tells Spring Security when this JWT filter
     * should be skipped completely.
     *
     * This is important for:
     * 1. Public auth APIs
     * 2. Browser CORS preflight requests
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        /*
         * Use request URI because it is more reliable in deployment.
         *
         * Example:
         * /api/auth/register
         * /api/auth/login
         * /api/asset-requests/my
         */
        String path = request.getRequestURI();

        /*
         * Skip JWT filter for public authentication routes.
         *
         * These endpoints are called before login,
         * so there will be no Authorization header.
         */
        if (path.startsWith("/api/auth")) {
            return true;
        }

        /*
         * Skip JWT filter for CORS preflight requests.
         *
         * Browser sends OPTIONS requests before POST/PUT/DELETE
         * when frontend and backend are hosted on different domains.
         */
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        /*
         * For all other requests, run the JWT filter.
         */
        return false;
    }

    /*
     * This method runs only for requests that are not skipped by shouldNotFilter().
     *
     * Example protected requests:
     * - GET /api/users/me
     * - POST /api/asset-requests
     * - GET /api/asset-requests/my
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        /*
         * Read Authorization header.
         *
         * Expected format:
         * Authorization: Bearer <jwt-token>
         */
        String authHeader = request.getHeader("Authorization");

        /*
         * If Authorization header is missing or does not start with "Bearer ",
         * continue without authentication.
         *
         * Spring Security will later block protected APIs automatically.
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
             * This email was stored inside the token during login.
             */
            String userEmail = jwtService.extractEmail(token);

            /*
             * Authenticate only if:
             * 1. Token contains email
             * 2. No authentication is already present in SecurityContext
             */
            if (
                    userEmail != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null
            ) {
                /*
                 * Load user from database.
                 *
                 * We need this to get the current role of the user.
                 */
                User user = userRepository
                        .findByEmail(userEmail)
                        .orElse(null);

                /*
                 * If user does not exist anymore,
                 * continue without authentication.
                 */
                if (user == null) {
                    filterChain.doFilter(request, response);
                    return;
                }

                /*
                 * Validate JWT token.
                 *
                 * Token is valid only if:
                 * 1. Token email matches user email
                 * 2. Token is not expired
                 * 3. Token signature is valid
                 */
                boolean tokenValid = jwtService.isTokenValid(token, userEmail);

                if (tokenValid) {

                    /*
                     * Convert database role into Spring Security role.
                     *
                     * Example:
                     * EMPLOYEE       -> ROLE_EMPLOYEE
                     * HR_MANAGER     -> ROLE_HR_MANAGER
                     * ADMIN          -> ROLE_ADMIN
                     * FINAL_APPROVER -> ROLE_FINAL_APPROVER
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
                     * - null because password is not needed for JWT request
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
                     * Attach request details such as remote address.
                     */
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    /*
                     * Store authentication in SecurityContext.
                     *
                     * After this, @PreAuthorize can check the user's role.
                     */
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authenticationToken);
                }
            }

        } catch (Exception ex) {
            /*
             * If token is invalid, expired, malformed, or cannot be parsed,
             * clear the SecurityContext.
             *
             * Do not throw an exception here.
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