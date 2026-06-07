package com.example.PrcureflowBackend.notification;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.PrcureflowBackend.assetrequest.AssetRequest;
import com.example.PrcureflowBackend.dealerquotation.DealerQuotation;
import com.example.PrcureflowBackend.finalapproval.FinalApproval;
import com.example.PrcureflowBackend.finalapproval.FinalApprovalDecision;
import com.example.PrcureflowBackend.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * EmailService sends all emails used in ProcureFlow.
 *
 * Earlier this project used Gmail SMTP:
 * Spring Boot -> smtp.gmail.com:587
 *
 * Render free blocks SMTP ports, so Gmail SMTP fails on Render.
 *
 * This version uses Resend HTTP API:
 * Spring Boot -> https://api.resend.com/emails
 *
 * HTTPS API works on Render free.
 */
@Service
public class EmailService {

    /*
     * Resend email API endpoint.
     */
    private static final String RESEND_EMAIL_API_URL = "https://api.resend.com/emails";

    /*
     * Resend API key.
     *
     * Add this in Render environment variables:
     * RESEND_API_KEY=re_xxxxxxxxxxxxxxxxx
     */
    @Value("${resend.api.key:}")
    private String resendApiKey;

    /*
     * Sender email.
     *
     * For quick testing:
     * RESEND_FROM_EMAIL=ProcureFlow <onboarding@resend.dev>
     *
     * Later, after verifying your own domain:
     * RESEND_FROM_EMAIL=ProcureFlow <no-reply@yourdomain.com>
     */
    @Value("${resend.from.email:ProcureFlow <onboarding@resend.dev>}")
    private String fromEmail;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public EmailService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        this.objectMapper = new ObjectMapper();
    }

    /*
     * Sends OTP email during registration.
     */
    public void sendOtpEmail(String toEmail, String otpCode) {

        String subject = "ProcureFlow Email Verification OTP";

        String body =
                "Your ProcureFlow verification OTP is: " + otpCode +
                "\n\nThis OTP will expire in 10 minutes.";

        sendEmail(toEmail, subject, body);
    }

    /*
     * Sends final approval/rejection result to employee.
     *
     * Used after the FINAL_APPROVER approves or rejects the asset request.
     */
    public void sendFinalDecisionEmail(
            User employee,
            AssetRequest assetRequest,
            FinalApproval finalApproval,
            DealerQuotation quotation
    ) {
        String subject;
        String body;

        if (finalApproval.getDecision() == FinalApprovalDecision.APPROVED) {

            subject = "ProcureFlow: Your Asset Request Has Been Approved";

            body =
                    "Hello " + employee.getName() + ",\n\n" +
                    "Your asset request has been approved.\n\n" +
                    "Asset: " + assetRequest.getAssetName() + "\n" +
                    "Quantity: " + assetRequest.getQuantity() + "\n" +
                    "Decision Reason: " + finalApproval.getReason() + "\n" +
                    "Dealer: " + quotation.getDealerName() + "\n" +
                    "Expected Delivery Days: " + quotation.getDeliveryDays() + "\n\n" +
                    "You can track the full request timeline in ProcureFlow.\n\n" +
                    "Regards,\nProcureFlow Team";

        } else {

            subject = "ProcureFlow: Your Asset Request Has Been Rejected";

            body =
                    "Hello " + employee.getName() + ",\n\n" +
                    "Your asset request has been rejected.\n\n" +
                    "Asset: " + assetRequest.getAssetName() + "\n" +
                    "Quantity: " + assetRequest.getQuantity() + "\n" +
                    "Rejection Reason: " + finalApproval.getReason() + "\n\n" +
                    "You can view the full request timeline in ProcureFlow.\n\n" +
                    "Regards,\nProcureFlow Team";
        }

        sendEmail(employee.getEmail(), subject, body);
    }

    /*
     * Sends delivery notification after HR marks the product as delivered.
     */
    public void sendDeliveryCompletedEmail(
            User employee,
            AssetRequest assetRequest
    ) {
        String subject = "ProcureFlow: Your Asset Has Been Delivered";

        String body =
                "Hello " + employee.getName() + ",\n\n" +
                "Your approved asset has been delivered.\n\n" +
                "Asset: " + assetRequest.getAssetName() + "\n" +
                "Quantity: " + assetRequest.getQuantity() + "\n\n" +
                "Please contact HR if there is any issue.\n\n" +
                "Regards,\nProcureFlow Team";

        sendEmail(employee.getEmail(), subject, body);
    }

    /*
     * Common reusable email method.
     *
     * Every project email should go through this method.
     */
    public void sendEmail(String toEmail, String subject, String body) {

        if (resendApiKey == null || resendApiKey.isBlank()) {
            throw new RuntimeException("RESEND_API_KEY is missing in environment variables");
        }

        if (fromEmail == null || fromEmail.isBlank()) {
            throw new RuntimeException("RESEND_FROM_EMAIL is missing in environment variables");
        }

        try {
            /*
             * Resend request body.
             *
             * This sends a plain text email.
             */
            Map<String, Object> requestBody = Map.of(
                    "from", fromEmail,
                    "to", List.of(toEmail),
                    "subject", subject,
                    "text", body
            );

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(RESEND_EMAIL_API_URL))
                    .timeout(Duration.ofSeconds(15))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            /*
             * Resend returns 2xx when the email is accepted.
             * Any non-2xx response means email sending failed.
             */
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(
                        "Resend email failed. Status: " +
                        response.statusCode() +
                        ", Response: " +
                        response.body()
                );
            }

        } catch (Exception ex) {
            throw new RuntimeException("Failed to send email using Resend API", ex);
        }
    }

    /*
     * Compatibility method.
     *
     * If any old code calls sendSimpleEmail(...),
     * this method keeps that code working.
     */
    public void sendSimpleEmail(String toEmail, String subject, String body) {
        sendEmail(toEmail, subject, body);
    }
}