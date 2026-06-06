package com.example.PrcureflowBackend.otp;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<Otp, Integer> {

    Optional<Otp> findTopByEmailAndOtpCodeAndUsedFalseOrderByIdDesc(
            String email,
            String otpCode
    );
}