package com.example.PrcureflowBackend.admin.dto;

/*
 * Request body used when Admin changes a user's role.
 *
 * Example:
 * {
 *   "role": "HR_MANAGER"
 * }
 */
public class UpdateUserRoleRequest {

    private String role;

    public UpdateUserRoleRequest() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}