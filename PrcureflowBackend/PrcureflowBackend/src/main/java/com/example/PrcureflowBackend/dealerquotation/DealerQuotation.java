package com.example.PrcureflowBackend.dealerquotation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.PrcureflowBackend.assetrequest.AssetRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/*
 * DealerQuotation represents the quotation received from a dealer.
 *
 * In V1, dealer does not login.
 * HR manually enters dealer quotation details into the system.
 */
@Entity
@Table(name = "dealer_quotations")
public class DealerQuotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /*
     * One asset request will have one dealer quotation in V1.
     *
     * unique = true means the same asset request cannot have
     * multiple dealer quotation records.
     */
    @OneToOne
    @JoinColumn(name = "asset_request_id", nullable = false, unique = true)
    private AssetRequest assetRequest;

    @Column(nullable = false)
    private String dealerName;

    /*
     * BigDecimal is preferred for money values.
     * Avoid double/float for currency.
     */
    @Column(nullable = false)
    private BigDecimal quotedPrice;

    /*
     * Number of days dealer needs to deliver the asset.
     */
    @Column(nullable = false)
    private int deliveryDays;

    @Column(length = 1000)
    private String warrantyDetails;

    @Column(length = 1000)
    private String dealerRemarks;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public DealerQuotation() {
    }

    public int getId() {
        return id;
    }

    public AssetRequest getAssetRequest() {
        return assetRequest;
    }

    public void setAssetRequest(AssetRequest assetRequest) {
        this.assetRequest = assetRequest;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public BigDecimal getQuotedPrice() {
        return quotedPrice;
    }

    public void setQuotedPrice(BigDecimal quotedPrice) {
        this.quotedPrice = quotedPrice;
    }

    public int getDeliveryDays() {
        return deliveryDays;
    }

    public void setDeliveryDays(int deliveryDays) {
        this.deliveryDays = deliveryDays;
    }

    public String getWarrantyDetails() {
        return warrantyDetails;
    }

    public void setWarrantyDetails(String warrantyDetails) {
        this.warrantyDetails = warrantyDetails;
    }

    public String getDealerRemarks() {
        return dealerRemarks;
    }

    public void setDealerRemarks(String dealerRemarks) {
        this.dealerRemarks = dealerRemarks;
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