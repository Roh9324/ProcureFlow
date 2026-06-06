package com.example.PrcureflowBackend.requesthistory.dto;

import java.time.LocalDateTime;

public class RequestStatusHistoryResponse {

    private int id;
    private int assetRequestId;
    private String oldStatus;
    private String newStatus;
    private String action;
    private String comment;
    private String changedByName;
    private String changedByEmail;
    private LocalDateTime changedAt;

    public RequestStatusHistoryResponse(
            int id,
            int assetRequestId,
            String oldStatus,
            String newStatus,
            String action,
            String comment,
            String changedByName,
            String changedByEmail,
            LocalDateTime changedAt
    ) {
        this.id = id;
        this.assetRequestId = assetRequestId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.action = action;
        this.comment = comment;
        this.changedByName = changedByName;
        this.changedByEmail = changedByEmail;
        this.changedAt = changedAt;
    }

    public int getId() {
        return id;
    }

    public int getAssetRequestId() {
        return assetRequestId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public String getAction() {
        return action;
    }

    public String getComment() {
        return comment;
    }

    public String getChangedByName() {
        return changedByName;
    }

    public String getChangedByEmail() {
        return changedByEmail;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }
}