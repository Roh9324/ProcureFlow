package com.example.PrcureflowBackend.finalapproval.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PendingFinalApprovalResponse {

    private int assetRequestId;
    private String assetName;
    private int quantity;
    private String reason;
    private String priority;
    private LocalDate neededByDate;
    private String employeeName;
    private String employeeEmail;
    private String status;

    private String dealerName;
    private BigDecimal quotedPrice;
    private int deliveryDays;
    private String warrantyDetails;
    private String dealerRemarks;

    public PendingFinalApprovalResponse(
            int assetRequestId,
            String assetName,
            int quantity,
            String reason,
            String priority,
            LocalDate neededByDate,
            String employeeName,
            String employeeEmail,
            String status,
            String dealerName,
            BigDecimal quotedPrice,
            int deliveryDays,
            String warrantyDetails,
            String dealerRemarks
    ) {
        this.assetRequestId = assetRequestId;
        this.assetName = assetName;
        this.quantity = quantity;
        this.reason = reason;
        this.priority = priority;
        this.neededByDate = neededByDate;
        this.employeeName = employeeName;
        this.employeeEmail = employeeEmail;
        this.status = status;
        this.dealerName = dealerName;
        this.quotedPrice = quotedPrice;
        this.deliveryDays = deliveryDays;
        this.warrantyDetails = warrantyDetails;
        this.dealerRemarks = dealerRemarks;
    }

    public int getAssetRequestId() {
        return assetRequestId;
    }

    public String getAssetName() {
        return assetName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getReason() {
        return reason;
    }

    public String getPriority() {
        return priority;
    }

    public LocalDate getNeededByDate() {
        return neededByDate;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public String getStatus() {
        return status;
    }

    public String getDealerName() {
        return dealerName;
    }

    public BigDecimal getQuotedPrice() {
        return quotedPrice;
    }

    public int getDeliveryDays() {
        return deliveryDays;
    }

    public String getWarrantyDetails() {
        return warrantyDetails;
    }

    public String getDealerRemarks() {
        return dealerRemarks;
    }
}