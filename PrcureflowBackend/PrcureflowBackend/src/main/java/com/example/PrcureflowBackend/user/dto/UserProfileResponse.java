package com.example.PrcureflowBackend.user.dto;

/*
 * UserProfileResponse is a DTO.
 *
 * DTO means Data Transfer Object.
 *
 * This class controls what user data is sent back to frontend.
 *
 * We do NOT send password in this response.
 */
public class UserProfileResponse {

    private int id;
    private String name;
    private String email;
    private String role;
    private boolean active;
    private boolean emailVerified;

    /*
     * Empty constructor is useful for frameworks like Jackson.
     */
    public UserProfileResponse() {
    }
    public UserProfileResponse(
            int id,
            String name,
            String email,
            String role,
            boolean active,
            boolean emailVerified
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.active = active;
        this.emailVerified = emailVerified;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }
}