package com.example.PrcureflowBackend.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.PrcureflowBackend.admin.dto.AdminUserResponse;
import com.example.PrcureflowBackend.admin.dto.UpdateUserRoleRequest;
import com.example.PrcureflowBackend.admin.dto.UpdateUserStatusRequest;

/*
 * AdminController exposes Admin-only APIs.
 *
 * Only users with ADMIN role can call these endpoints.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /*
     * GET /api/admin/users
     *
     * Returns all users.
     */
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /*
     * PUT /api/admin/users/{userId}/role
     *
     * Changes user role.
     *
     * Example body:
     * {
     *   "role": "HR_MANAGER"
     * }
     */
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<AdminUserResponse> updateUserRole(
            @PathVariable int userId,
            @RequestBody UpdateUserRoleRequest request
    ) {
        return ResponseEntity.ok(
                adminService.updateUserRole(userId, request)
        );
    }

    /*
     * PUT /api/admin/users/{userId}/status
     *
     * Activates or deactivates user.
     *
     * Example body:
     * {
     *   "active": false
     * }
     */
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<AdminUserResponse> updateUserStatus(
            @PathVariable int userId,
            @RequestBody UpdateUserStatusRequest request
    ) {
        return ResponseEntity.ok(
                adminService.updateUserStatus(userId, request)
        );
    }
}