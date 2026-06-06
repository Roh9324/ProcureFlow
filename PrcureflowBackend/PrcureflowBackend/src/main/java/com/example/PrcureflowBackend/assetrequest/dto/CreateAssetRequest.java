package com.example.PrcureflowBackend.assetrequest.dto;

import java.time.LocalDate;

import com.example.PrcureflowBackend.assetrequest.AssetPriority;

public class CreateAssetRequest {

    private String assetName;
    private int quantity;
    private String reason;
    private AssetPriority priority;
    private LocalDate neededByDate;

    public CreateAssetRequest() {
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


    public AssetPriority getPriority() {
        return priority;
    }

    public void setPriority(AssetPriority priority) {
        this.priority = priority;
    }


    public LocalDate getNeededByDate() {
        return neededByDate;
    }

    public void setNeededByDate(LocalDate neededByDate) {
        this.neededByDate = neededByDate;
    }
}