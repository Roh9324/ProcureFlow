package com.example.PrcureflowBackend.assetrequest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AssetRequestResponse {

    private int id;
    private String assetName;
    private int quantity;
    private String reason;
    private String priority;
    private LocalDate neededByDate;
    private String status;
    private String createdByName;
    private String createdByEmail;
    private LocalDateTime createdAt;
    private String hrRemarks;

    public AssetRequestResponse(
            int id,
            String assetName,
            int quantity,
            String reason,
            String priority,
            LocalDate neededByDate,
            String status,
            String createdByName,
            String createdByEmail,
            LocalDateTime createdAt,
            String hrRemarks
    ) {
        this.id = id;
        this.assetName = assetName;
        this.quantity = quantity;
        this.reason = reason;
        this.priority = priority;
        this.neededByDate = neededByDate;
        this.status = status;
        this.createdByName = createdByName;
        this.createdByEmail = createdByEmail;
        this.createdAt = createdAt;
        this.hrRemarks = hrRemarks;
    }

    public int getId() {
        return id;
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

    public String getStatus() {
        return status;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public String getCreatedByEmail() {
        return createdByEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getHrRemarks() {
        return hrRemarks;
    }
}