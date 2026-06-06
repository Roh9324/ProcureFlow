package com.example.PrcureflowBackend.finalapproval.dto;

import java.time.LocalDateTime;

public class FinalApprovalResponse {

    private int approvalId;
    private int assetRequestId;
    private String assetName;
    private String decision;
    private String reason;
    private String approverName;
    private String approverEmail;
    private String requestStatus;
    private LocalDateTime decidedAt;

    public FinalApprovalResponse(
            int approvalId,
            int assetRequestId,
            String assetName,
            String decision,
            String reason,
            String approverName,
            String approverEmail,
            String requestStatus,
            LocalDateTime decidedAt
    ) {
        this.approvalId = approvalId;
        this.assetRequestId = assetRequestId;
        this.assetName = assetName;
        this.decision = decision;
        this.reason = reason;
        this.approverName = approverName;
        this.approverEmail = approverEmail;
        this.requestStatus = requestStatus;
        this.decidedAt = decidedAt;
    }

    public int getApprovalId() {
        return approvalId;
    }

    public int getAssetRequestId() {
        return assetRequestId;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getDecision() {
        return decision;
    }

    public String getReason() {
        return reason;
    }

    public String getApproverName() {
        return approverName;
    }

    public String getApproverEmail() {
        return approverEmail;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }
}