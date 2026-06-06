package com.example.PrcureflowBackend.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.PrcureflowBackend.admin.dto.AdminUserResponse;
import com.example.PrcureflowBackend.admin.dto.UpdateUserRoleRequest;
import com.example.PrcureflowBackend.admin.dto.UpdateUserStatusRequest;
import com.example.PrcureflowBackend.role.Role;
import com.example.PrcureflowBackend.role.RoleName;
import com.example.PrcureflowBackend.role.RoleRepository;
import com.example.PrcureflowBackend.user.User;
import com.example.PrcureflowBackend.user.UserRepository;

/*
 * AdminService contains business logic for Admin features.
 *
 * Admin can:
 * 1. View all users
 * 2. Change user roles
 * 3. Activate/deactivate users
 */
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminService(
            UserRepository userRepository,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /*
     * Returns all users for Admin dashboard.
     */
    public List<AdminUserResponse> getAllUsers() {

        return userRepository
                .findAll()
                .stream()
                .map(this::mapToAdminUserResponse)
                .collect(Collectors.toList());
    }

    /*
     * Updates user role.
     *
     * Example:
     * EMPLOYEE -> HR_MANAGER
     */
    public AdminUserResponse updateUserRole(
            int userId,
            UpdateUserRoleRequest request
    ) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RoleName requestedRole;

        try {
            requestedRole = RoleName.valueOf(request.getRole().trim().toUpperCase());
        } catch (Exception ex) {
            throw new RuntimeException("Invalid role. Allowed roles: EMPLOYEE, HR_MANAGER, FINAL_APPROVER, ADMIN");
        }

        Role role = roleRepository
                .findByName(requestedRole)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRole(role);

        User updatedUser = userRepository.save(user);

        return mapToAdminUserResponse(updatedUser);
    }

    /*
     * Activates or deactivates user account.
     *
     * If active = false, user should not be allowed to login.
     */
    public AdminUserResponse updateUserStatus(
            int userId,
            UpdateUserStatusRequest request
    ) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(request.isActive());

        User updatedUser = userRepository.save(user);

        return mapToAdminUserResponse(updatedUser);
    }

    /*
     * Converts User entity to AdminUserResponse DTO.
     */
    private AdminUserResponse mapToAdminUserResponse(User user) {

        String roleName = user.getRole() != null
                ? user.getRole().getName().name()
                : null;

        return new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                roleName,
                user.isActive(),
                user.isEmailVerified()
        );
    }
}