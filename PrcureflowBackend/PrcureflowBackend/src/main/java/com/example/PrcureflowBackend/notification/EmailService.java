package com.example.PrcureflowBackend.notification;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.PrcureflowBackend.assetrequest.AssetRequest;
import com.example.PrcureflowBackend.dealerquotation.DealerQuotation;
import com.example.PrcureflowBackend.finalapproval.FinalApproval;
import com.example.PrcureflowBackend.finalapproval.FinalApprovalDecision;
import com.example.PrcureflowBackend.user.User;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /*
     * Sends OTP email during registration.
     */
    public void sendOtpEmail(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("ProcureFlow Email Verification OTP");
        message.setText(
                "Your ProcureFlow verification OTP is: " + otpCode +
                "\n\nThis OTP will expire in 10 minutes."
        );

        mailSender.send(message);
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
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(employee.getEmail());

        if (finalApproval.getDecision() == FinalApprovalDecision.APPROVED) {
            message.setSubject("ProcureFlow: Your Asset Request Has Been Approved");

            message.setText(
                    "Hello " + employee.getName() + ",\n\n" +
                    "Your asset request has been approved.\n\n" +
                    "Asset: " + assetRequest.getAssetName() + "\n" +
                    "Quantity: " + assetRequest.getQuantity() + "\n" +
                    "Decision Reason: " + finalApproval.getReason() + "\n" +
                    "Dealer: " + quotation.getDealerName() + "\n" +
                    "Expected Delivery Days: " + quotation.getDeliveryDays() + "\n\n" +
                    "You can track the full request timeline in ProcureFlow.\n\n" +
                    "Regards,\nProcureFlow Team"
            );
        } else {
            message.setSubject("ProcureFlow: Your Asset Request Has Been Rejected");

            message.setText(
                    "Hello " + employee.getName() + ",\n\n" +
                    "Your asset request has been rejected.\n\n" +
                    "Asset: " + assetRequest.getAssetName() + "\n" +
                    "Quantity: " + assetRequest.getQuantity() + "\n" +
                    "Rejection Reason: " + finalApproval.getReason() + "\n\n" +
                    "You can view the full request timeline in ProcureFlow.\n\n" +
                    "Regards,\nProcureFlow Team"
            );
        }

        mailSender.send(message);
    }

    /*
     * Sends delivery notification after HR marks the product as delivered.
     */
    public void sendDeliveryCompletedEmail(
            User employee,
            AssetRequest assetRequest
    ) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(employee.getEmail());
        message.setSubject("ProcureFlow: Your Asset Has Been Delivered");

        message.setText(
                "Hello " + employee.getName() + ",\n\n" +
                "Your approved asset has been delivered.\n\n" +
                "Asset: " + assetRequest.getAssetName() + "\n" +
                "Quantity: " + assetRequest.getQuantity() + "\n\n" +
                "Please contact HR if there is any issue.\n\n" +
                "Regards,\nProcureFlow Team"
        );

        mailSender.send(message);
    }
}