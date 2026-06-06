package com.example.PrcureflowBackend.admin.dto;

/*
 * AdminUserResponse is used to send user data to Admin UI.
 *
 * We do not expose password.
 */
public class AdminUserResponse {

    private int id;
    private String name;
    private String email;
    private String role;
    private boolean active;
    private boolean emailVerified;

    public AdminUserResponse(
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