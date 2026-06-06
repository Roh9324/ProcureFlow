package com.example.PrcureflowBackend.dealerquotation.dto;

import java.math.BigDecimal;

/*
 * Request body used when HR enters dealer quotation.
 *
 * Example:
 * {
 *   "dealerName": "ABC Computers",
 *   "quotedPrice": 65000,
 *   "deliveryDays": 5,
 *   "warrantyDetails": "1 year warranty",
 *   "dealerRemarks": "Available in stock"
 * }
 */
public class DealerQuotationRequest {

    private String dealerName;
    private BigDecimal quotedPrice;
    private int deliveryDays;
    private String warrantyDetails;
    private String dealerRemarks;

    public DealerQuotationRequest() {
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
}