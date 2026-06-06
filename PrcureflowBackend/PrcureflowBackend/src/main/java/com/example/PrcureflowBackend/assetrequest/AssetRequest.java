package com.example.PrcureflowBackend.assetrequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.PrcureflowBackend.user.User;

import jakarta.persistence.*;

@Entity
@Table(name = "asset_requests")
public class AssetRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String assetName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetPriority priority;

    private LocalDate neededByDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetRequestStatus status;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    @Column(length = 1000)
    private String hrRemarks;

    public String getHrRemarks() {
		return hrRemarks;
	}

	public void setHrRemarks(String hrRemarks) {
		this.hrRemarks = hrRemarks;
	}

	public AssetRequest() {
    }

    public int getId() {
        return id;
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


    public AssetRequestStatus getStatus() {
        return status;
    }

    public void setStatus(AssetRequestStatus status) {
        this.status = status;
    }


    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}