package com.example.PrcureflowBackend.finalapproval.dto;

public class FinalApprovalDecisionRequest {

    private String decision;
    private String reason;

    public FinalApprovalDecisionRequest() {
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}