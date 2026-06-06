package com.example.PrcureflowBackend.auth.dto;

public class VerifyOtpRequest {

    private String email;
    private String otpCode;

    public VerifyOtpRequest() {
    }

    public String getEmail() {
        return email;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}