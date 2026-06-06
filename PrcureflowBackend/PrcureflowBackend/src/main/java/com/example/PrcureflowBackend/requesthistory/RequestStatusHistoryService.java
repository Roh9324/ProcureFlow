package com.example.PrcureflowBackend.requesthistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.PrcureflowBackend.assetrequest.AssetRequest;
import com.example.PrcureflowBackend.assetrequest.AssetRequestRepository;
import com.example.PrcureflowBackend.assetrequest.AssetRequestStatus;
import com.example.PrcureflowBackend.requesthistory.dto.RequestStatusHistoryResponse;
import com.example.PrcureflowBackend.role.RoleName;
import com.example.PrcureflowBackend.user.User;
import com.example.PrcureflowBackend.user.UserRepository;

@Service
public class RequestStatusHistoryService {

    private final RequestStatusHistoryRepository historyRepository;
    private final AssetRequestRepository assetRequestRepository;
    private final UserRepository userRepository;

    public RequestStatusHistoryService(
            RequestStatusHistoryRepository historyRepository,
            AssetRequestRepository assetRequestRepository,
            UserRepository userRepository
    ) {
        this.historyRepository = historyRepository;
        this.assetRequestRepository = assetRequestRepository;
        this.userRepository = userRepository;
    }

    /*
     * Creates one timeline entry whenever request status changes.
     */
    public void recordStatusChange(
            AssetRequest assetRequest,
            AssetRequestStatus oldStatus,
            AssetRequestStatus newStatus,
            User changedBy,
            String action,
            String comment
    ) {
        RequestStatusHistory history = new RequestStatusHistory();

        history.setAssetRequest(assetRequest);
        history.setOldStatus(oldStatus != null ? oldStatus.name() : null);
        history.setNewStatus(newStatus.name());
        history.setChangedBy(changedBy);
        history.setAction(action);
        history.setComment(comment);
        history.setChangedAt(LocalDateTime.now());

        historyRepository.save(history);
    }

    /*
     * Returns timeline for a request.
     *
     * EMPLOYEE can see only their own request history.
     * HR_MANAGER, FINAL_APPROVER, and ADMIN can see all request histories.
     */
    public List<RequestStatusHistoryResponse> getHistoryForRequest(
            int assetRequestId,
            String userEmail
    ) {
        AssetRequest assetRequest = assetRequestRepository
                .findById(assetRequestId)
                .orElseThrow(() -> new RuntimeException("Asset request not found"));

        User loggedInUser = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RoleName roleName = loggedInUser.getRole().getName();

        if (roleName == RoleName.EMPLOYEE &&
                !assetRequest.getCreatedBy().getEmail().equals(userEmail)) {
            throw new RuntimeException("You are not allowed to view this request history");
        }

        return historyRepository
                .findByAssetRequestIdOrderByChangedAtAsc(assetRequestId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private RequestStatusHistoryResponse mapToResponse(RequestStatusHistory history) {

        return new RequestStatusHistoryResponse(
                history.getId(),
                history.getAssetRequest().getId(),
                history.getOldStatus(),
                history.getNewStatus(),
                history.getAction(),
                history.getComment(),
                history.getChangedBy().getName(),
                history.getChangedBy().getEmail(),
                history.getChangedAt()
        );
    }
}