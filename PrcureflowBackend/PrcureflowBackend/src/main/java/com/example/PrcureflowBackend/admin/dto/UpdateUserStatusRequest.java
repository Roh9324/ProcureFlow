package com.example.PrcureflowBackend.admin.dto;

/*
 * Request body used when Admin activates or deactivates a user.
 *
 * Example:
 * {
 *   "active": false
 * }
 */
public class UpdateUserStatusRequest {

    private boolean active;

    public UpdateUserStatusRequest() {
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}