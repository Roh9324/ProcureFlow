package com.example.PrcureflowBackend.finalapproval;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.PrcureflowBackend.assetrequest.AssetRequest;
import com.example.PrcureflowBackend.assetrequest.AssetRequestRepository;
import com.example.PrcureflowBackend.assetrequest.AssetRequestStatus;
import com.example.PrcureflowBackend.dealerquotation.DealerQuotation;
import com.example.PrcureflowBackend.dealerquotation.DealerQuotationRepository;
import com.example.PrcureflowBackend.finalapproval.dto.FinalApprovalDecisionRequest;
import com.example.PrcureflowBackend.finalapproval.dto.FinalApprovalResponse;
import com.example.PrcureflowBackend.finalapproval.dto.PendingFinalApprovalResponse;
import com.example.PrcureflowBackend.requesthistory.RequestStatusHistoryService;
import com.example.PrcureflowBackend.user.User;
import com.example.PrcureflowBackend.user.UserRepository;

@Service
public class FinalApprovalService {

    private final AssetRequestRepository assetRequestRepository;
    private final DealerQuotationRepository dealerQuotationRepository;
    private final FinalApprovalRepository finalApprovalRepository;
    private final UserRepository userRepository;
    private final RequestStatusHistoryService historyService;

    /*
     * Constructor injection.
     *
     * Spring injects all required repositories/services.
     */
    public FinalApprovalService(
            AssetRequestRepository assetRequestRepository,
            DealerQuotationRepository dealerQuotationRepository,
            FinalApprovalRepository finalApprovalRepository,
            UserRepository userRepository,
            RequestStatusHistoryService historyService
    ) {
        this.assetRequestRepository = assetRequestRepository;
        this.dealerQuotationRepository = dealerQuotationRepository;
        this.finalApprovalRepository = finalApprovalRepository;
        this.userRepository = userRepository;
        this.historyService = historyService;
    }

    /*
     * Final approver views requests waiting for final decision.
     *
     * Only requests with status SENT_FOR_FINAL_APPROVAL are shown.
     */
    public List<PendingFinalApprovalResponse> getPendingApprovals() {

        return assetRequestRepository
                .findByStatusOrderByUpdatedAtDesc(AssetRequestStatus.SENT_FOR_FINAL_APPROVAL)
                .stream()
                .map(this::mapToPendingResponse)
                .collect(Collectors.toList());
    }

    /*
     * Final approver approves or rejects request.
     *
     * Status movement:
     * SENT_FOR_FINAL_APPROVAL -> FINAL_APPROVED
     * or
     * SENT_FOR_FINAL_APPROVAL -> FINAL_REJECTED
     */
    public FinalApprovalResponse decide(
            int assetRequestId,
            FinalApprovalDecisionRequest request,
            String approverEmail
    ) {
        /*
         * Find asset request.
         */
        AssetRequest assetRequest = assetRequestRepository
                .findById(assetRequestId)
                .orElseThrow(() -> new RuntimeException("Asset request not found"));

        /*
         * Final decision can be made only after HR sends request for final approval.
         */
        if (assetRequest.getStatus() != AssetRequestStatus.SENT_FOR_FINAL_APPROVAL) {
            throw new RuntimeException("Only requests sent for final approval can be decided");
        }

        /*
         * In V1, one request can have only one final approval decision.
         */
        if (finalApprovalRepository.existsByAssetRequestId(assetRequestId)) {
            throw new RuntimeException("Final approval decision already exists for this request");
        }

        /*
         * Find logged-in final approver.
         */
        User approver = userRepository
                .findByEmail(approverEmail)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        /*
         * Convert request decision string into enum.
         *
         * Allowed:
         * APPROVED
         * REJECTED
         */
        FinalApprovalDecision decision;

        try {
            decision = FinalApprovalDecision.valueOf(
                    request.getDecision().trim().toUpperCase()
            );
        } catch (Exception ex) {
            throw new RuntimeException("Invalid decision. Allowed values: APPROVED, REJECTED");
        }

        /*
         * Save old status before changing it.
         */
        AssetRequestStatus oldStatus = assetRequest.getStatus();

        /*
         * Create final approval record.
         */
        FinalApproval finalApproval = new FinalApproval();
        finalApproval.setAssetRequest(assetRequest);
        finalApproval.setApprover(approver);
        finalApproval.setDecision(decision);
        finalApproval.setReason(request.getReason());
        finalApproval.setDecidedAt(LocalDateTime.now());

        FinalApproval savedApproval = finalApprovalRepository.save(finalApproval);

        /*
         * Update asset request status based on decision.
         */
        if (decision == FinalApprovalDecision.APPROVED) {
            assetRequest.setStatus(AssetRequestStatus.FINAL_APPROVED);
        } else {
            assetRequest.setStatus(AssetRequestStatus.FINAL_REJECTED);
        }

        assetRequest.setUpdatedAt(LocalDateTime.now());

        assetRequestRepository.save(assetRequest);

        /*
         * Record timeline/history entry.
         *
         * This allows employee to see final approval/rejection result
         * along with the final approver's reason.
         */
        historyService.recordStatusChange(
                assetRequest,
                oldStatus,
                assetRequest.getStatus(),
                approver,
                decision == FinalApprovalDecision.APPROVED
                        ? "Final Approval Approved"
                        : "Final Approval Rejected",
                request.getReason()
        );

        return mapToFinalApprovalResponse(savedApproval);
    }

    /*
     * Converts asset request + dealer quotation into pending approval response.
     */
    private PendingFinalApprovalResponse mapToPendingResponse(AssetRequest assetRequest) {

        DealerQuotation quotation = dealerQuotationRepository
                .findByAssetRequestId(assetRequest.getId())
                .orElseThrow(() -> new RuntimeException("Dealer quotation not found"));

        return new PendingFinalApprovalResponse(
                assetRequest.getId(),
                assetRequest.getAssetName(),
                assetRequest.getQuantity(),
                assetRequest.getReason(),
                assetRequest.getPriority().name(),
                assetRequest.getNeededByDate(),
                assetRequest.getCreatedBy().getName(),
                assetRequest.getCreatedBy().getEmail(),
                assetRequest.getStatus().name(),
                quotation.getDealerName(),
                quotation.getQuotedPrice(),
                quotation.getDeliveryDays(),
                quotation.getWarrantyDetails(),
                quotation.getDealerRemarks()
        );
    }

    /*
     * Converts FinalApproval entity into response DTO.
     */
    private FinalApprovalResponse mapToFinalApprovalResponse(FinalApproval approval) {

        AssetRequest assetRequest = approval.getAssetRequest();

        return new FinalApprovalResponse(
                approval.getId(),
                assetRequest.getId(),
                assetRequest.getAssetName(),
                approval.getDecision().name(),
                approval.getReason(),
                approval.getApprover().getName(),
                approval.getApprover().getEmail(),
                assetRequest.getStatus().name(),
                approval.getDecidedAt()
        );
    }
}