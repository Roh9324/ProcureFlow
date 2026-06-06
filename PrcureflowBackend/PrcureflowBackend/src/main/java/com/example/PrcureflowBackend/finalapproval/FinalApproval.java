package com.example.PrcureflowBackend.finalapproval;

import java.time.LocalDateTime;

import com.example.PrcureflowBackend.assetrequest.AssetRequest;
import com.example.PrcureflowBackend.user.User;

import jakarta.persistence.*;

@Entity
@Table(name = "final_approvals")
public class FinalApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /*
     * One asset request has one final approval decision in V1.
     */
    @OneToOne
    @JoinColumn(name = "asset_request_id", nullable = false, unique = true)
    private AssetRequest assetRequest;

    /*
     * User who gave the final approval/rejection.
     */
    @ManyToOne
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FinalApprovalDecision decision;

    /*
     * Reason written by final approver.
     */
    @Column(nullable = false, length = 1000)
    private String reason;

    private LocalDateTime decidedAt;

    public FinalApproval() {
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

    public User getApprover() {
        return approver;
    }

    public void setApprover(User approver) {
        this.approver = approver;
    }

    public FinalApprovalDecision getDecision() {
        return decision;
    }

    public void setDecision(FinalApprovalDecision decision) {
        this.decision = decision;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(LocalDateTime decidedAt) {
        this.decidedAt = decidedAt;
    }
}