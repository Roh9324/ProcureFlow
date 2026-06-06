package com.example.PrcureflowBackend.auth;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.PrcureflowBackend.auth.dto.AuthResponse;
import com.example.PrcureflowBackend.auth.dto.LoginRequest;
import com.example.PrcureflowBackend.auth.dto.RegisterRequest;
import com.example.PrcureflowBackend.auth.dto.VerifyOtpRequest;
import com.example.PrcureflowBackend.notification.EmailService;
import com.example.PrcureflowBackend.otp.Otp;
import com.example.PrcureflowBackend.otp.OtpRepository;
import com.example.PrcureflowBackend.role.Role;
import com.example.PrcureflowBackend.role.RoleName;
import com.example.PrcureflowBackend.role.RoleRepository;
import com.example.PrcureflowBackend.security.JwtService;
import com.example.PrcureflowBackend.user.User;
import com.example.PrcureflowBackend.user.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;

    public AuthService(
            UserRepository userRepository,
            OtpRepository otpRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.roleRepository = roleRepository;
    }

    // Register User
    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Role employeeRole = roleRepository
                .findByName(RoleName.EMPLOYEE)
                .orElseThrow(() -> new RuntimeException("Default role EMPLOYEE not found"));

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setEmailVerified(false);
        user.setRole(employeeRole);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        String otpCode = generateOtp();

        Otp otp = new Otp();
        otp.setEmail(request.getEmail());
        otp.setOtpCode(otpCode);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        otp.setUsed(false);

        otpRepository.save(otp);

        emailService.sendOtpEmail(request.getEmail(), otpCode);

        return "Registration successful. OTP sent to email.";
    }

    // Generate 6-digit OTP
    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    // Verify OTP
    public String verifyOtp(VerifyOtpRequest request) {

        Otp otp = otpRepository
                .findTopByEmailAndOtpCodeAndUsedFalseOrderByIdDesc(
                        request.getEmail(),
                        request.getOtpCode()
                )
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired");
        }

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmailVerified(true);
        user.setUpdatedAt(LocalDateTime.now());

        otp.setUsed(true);

        userRepository.save(user);
        otpRepository.save(otp);

        return "Email verified successfully. You can now login.";
    }

    // Login User
    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.isEmailVerified()) {
            throw new RuntimeException("Please verify your email before login");
        }

        if (!user.isActive()) {
            throw new RuntimeException("User account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        String roleName = user.getRole() != null
                ? user.getRole().getName().name()
                : null;

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getName(),
                roleName
        );
    }
}