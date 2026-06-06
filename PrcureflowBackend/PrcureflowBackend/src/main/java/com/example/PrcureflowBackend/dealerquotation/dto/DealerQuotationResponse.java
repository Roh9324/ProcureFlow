package com.example.PrcureflowBackend.dealerquotation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * Response sent back after dealer quotation is created or fetched.
 */
public class DealerQuotationResponse {

    private int id;
    private int assetRequestId;
    private String assetName;
    private String dealerName;
    private BigDecimal quotedPrice;
    private int deliveryDays;
    private String warrantyDetails;
    private String dealerRemarks;
    private String requestStatus;
    private LocalDateTime createdAt;

    public DealerQuotationResponse(
            int id,
            int assetRequestId,
            String assetName,
            String dealerName,
            BigDecimal quotedPrice,
            int deliveryDays,
            String warrantyDetails,
            String dealerRemarks,
            String requestStatus,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.assetRequestId = assetRequestId;
        this.assetName = assetName;
        this.dealerName = dealerName;
        this.quotedPrice = quotedPrice;
        this.deliveryDays = deliveryDays;
        this.warrantyDetails = warrantyDetails;
        this.dealerRemarks = dealerRemarks;
        this.requestStatus = requestStatus;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getAssetRequestId() {
        return assetRequestId;
    }

    public String getAssetName() {
        return assetName;
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

    public String getRequestStatus() {
        return requestStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}