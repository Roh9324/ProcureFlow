package com.example.PrcureflowBackend.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.PrcureflowBackend.role.Role;
import com.example.PrcureflowBackend.role.RoleName;
import com.example.PrcureflowBackend.role.RoleRepository;
import com.example.PrcureflowBackend.user.User;
import com.example.PrcureflowBackend.user.UserRepository;

/*
 * DataSeeder runs automatically when the Spring Boot application starts.
 *
 * Its purpose:
 * 1. Create default roles if they do not exist
 * 2. Create one default ADMIN user
 *
 * This helps us avoid manually inserting roles/admin user from PostgreSQL.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        /*
         * Create default roles.
         */
        createRoleIfNotExists(RoleName.EMPLOYEE, "Employee who can create asset requests");
        createRoleIfNotExists(RoleName.HR_MANAGER, "HR/Manager who handles asset requests and dealer quotations");
        createRoleIfNotExists(RoleName.FINAL_APPROVER, "Owner or top-level authority who gives final approval");
        createRoleIfNotExists(RoleName.ADMIN, "System administrator");

        /*
         * Create default admin user.
         */
        createDefaultAdminUser();
    }

    private void createRoleIfNotExists(RoleName roleName, String description) {

        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role(roleName, description);
            roleRepository.save(role);
        }
    }

    private void createDefaultAdminUser() {

        String adminEmail = "mohitmishra260503@gmail.com";
        String adminPassword = "Admin@123";

        Role adminRole = roleRepository
                .findByName(RoleName.ADMIN)
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        /*
         * If admin already exists, make sure it has ADMIN role,
         * is active, and email is verified.
         */
        User existingAdmin = userRepository
                .findByEmail(adminEmail)
                .orElse(null);

        if (existingAdmin != null) {
            existingAdmin.setRole(adminRole);
            existingAdmin.setActive(true);
            existingAdmin.setEmailVerified(true);
            existingAdmin.setUpdatedAt(LocalDateTime.now());

            userRepository.save(existingAdmin);
            return;
        }

        /*
         * Create new admin user if not present.
         */
        User admin = new User();

        admin.setName("System Admin");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(adminRole);
        admin.setActive(true);
        admin.setEmailVerified(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        userRepository.save(admin);
    }
}