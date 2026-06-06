package com.example.PrcureflowBackend.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PrcureflowBackend.user.dto.UserProfileResponse;
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    /*
     * Constructor injection.
     *
     * Spring injects UserRepository automatically.
     */
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
     * GET /api/users/me
     *
     * This API returns logged-in user details.
     *
     * It requires JWT token in request header:
     * Authorization: Bearer your_token_here
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(Authentication authentication) {

        /*
         * Authentication object comes from Spring Security.
         *
         * In JwtAuthenticationFilter, we stored user email as principal.
         *
         * So authentication.getName() returns logged-in user's email.
         */
        String email = authentication.getName();

        /*
         * Find full user data from database using email.
         */
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        /*
         * Convert role enum to String.
         *
         * Example:
         * RoleName.EMPLOYEE becomes "EMPLOYEE"
         */
        String roleName = user.getRole() != null
                ? user.getRole().getName().name()
                : null;

        /*
         * Create response DTO.
         *
         * Notice:
         * We do not expose password.
         */
        UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                roleName,
                user.isActive(),
                user.isEmailVerified()
        );

        /*
         * Return response as JSON.
         */
        return ResponseEntity.ok(response);
    }
}