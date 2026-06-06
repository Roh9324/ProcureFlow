package com.example.PrcureflowBackend.requesthistory;

import java.time.LocalDateTime;

import com.example.PrcureflowBackend.assetrequest.AssetRequest;
import com.example.PrcureflowBackend.user.User;

import jakarta.persistence.*;

@Entity
@Table(name = "request_status_history")
public class RequestStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /*
     * The asset request whose status changed.
     */
    @ManyToOne
    @JoinColumn(name = "asset_request_id", nullable = false)
    private AssetRequest assetRequest;

    /*
     * Previous status.
     * This can be null for the first history entry when request is created.
     */
    private String oldStatus;

    /*
     * New status after the action.
     */
    @Column(nullable = false)
    private String newStatus;

    /*
     * Human-readable action name.
     * Example: "Request Submitted", "HR Review Started"
     */
    @Column(nullable = false)
    private String action;

    /*
     * Optional comment.
     * Example: HR remarks, approver reason, etc.
     */
    @Column(length = 1000)
    private String comment;

    /*
     * User who performed the action.
     */
    @ManyToOne
    @JoinColumn(name = "changed_by_user_id", nullable = false)
    private User changedBy;

    private LocalDateTime changedAt;

    public RequestStatusHistory() {
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

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}