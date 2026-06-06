package com.example.PrcureflowBackend.assetrequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.PrcureflowBackend.assetrequest.dto.AssetRequestResponse;
import com.example.PrcureflowBackend.assetrequest.dto.CreateAssetRequest;
import com.example.PrcureflowBackend.assetrequest.dto.HrReviewRequest;
import com.example.PrcureflowBackend.dealerquotation.DealerQuotationRepository;
import com.example.PrcureflowBackend.requesthistory.RequestStatusHistoryService;
import com.example.PrcureflowBackend.user.User;
import com.example.PrcureflowBackend.user.UserRepository;
import com.example.PrcureflowBackend.dealerquotation.DealerQuotation;
import com.example.PrcureflowBackend.finalapproval.FinalApproval;
import com.example.PrcureflowBackend.finalapproval.FinalApprovalRepository;
import com.example.PrcureflowBackend.notification.EmailService;

@Service
public class AssetRequestService {

    private final AssetRequestRepository assetRequestRepository;
    private final UserRepository userRepository;
    private final DealerQuotationRepository dealerQuotationRepository;
    private final RequestStatusHistoryService historyService;
    private final FinalApprovalRepository finalApprovalRepository;
    private final EmailService emailService;
    /*
     * Constructor injection.
     *
     * Spring automatically provides:
     * - AssetRequestRepository
     * - UserRepository
     * - DealerQuotationRepository
     * - RequestStatusHistoryService
     */
    public AssetRequestService(
            AssetRequestRepository assetRequestRepository,
            UserRepository userRepository,
            DealerQuotationRepository dealerQuotationRepository,
            RequestStatusHistoryService historyService,
            FinalApprovalRepository finalApprovalRepository,
            EmailService emailService
    ) {
        this.assetRequestRepository = assetRequestRepository;
        this.userRepository = userRepository;
        this.dealerQuotationRepository = dealerQuotationRepository;
        this.historyService = historyService;
        this.finalApprovalRepository = finalApprovalRepository;
        this.emailService = emailService;
    }

    /*
     * Employee creates a new asset request.
     *
     * The frontend does not send userId.
     * The logged-in user's email comes from the JWT token.
     */
    public AssetRequestResponse createRequest(CreateAssetRequest request, String userEmail) {

        /*
         * Find the logged-in employee from the database.
         */
        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        /*
         * Create a new AssetRequest entity and fill it with request data.
         */
        AssetRequest assetRequest = new AssetRequest();

        assetRequest.setAssetName(request.getAssetName());
        assetRequest.setQuantity(request.getQuantity());
        assetRequest.setReason(request.getReason());
        assetRequest.setPriority(request.getPriority());
        assetRequest.setNeededByDate(request.getNeededByDate());

        /*
         * Every new request starts with REQUEST_SUBMITTED.
         */
        assetRequest.setStatus(AssetRequestStatus.REQUEST_SUBMITTED);

        /*
         * Link the request to the logged-in employee.
         */
        assetRequest.setCreatedBy(user);

        assetRequest.setCreatedAt(LocalDateTime.now());
        assetRequest.setUpdatedAt(LocalDateTime.now());

        /*
         * Save the newly created asset request.
         */
        AssetRequest savedRequest = assetRequestRepository.save(assetRequest);

        /*
         * Create the first timeline entry.
         *
         * oldStatus = null because this is the first status.
         * newStatus = REQUEST_SUBMITTED because the employee just submitted it.
         */
        historyService.recordStatusChange(
                savedRequest,
                null,
                AssetRequestStatus.REQUEST_SUBMITTED,
                user,
                "Request Submitted",
                "Employee submitted asset request"
        );

        /*
         * Return clean response DTO to frontend.
         */
        return mapToResponse(savedRequest);
    }

    /*
     * Employee views only their own asset requests.
     */
    public List<AssetRequestResponse> getMyRequests(String userEmail) {

        return assetRequestRepository
                .findByCreatedByEmailOrderByCreatedAtDesc(userEmail)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /*
     * HR_MANAGER or ADMIN views all asset requests.
     */
    public List<AssetRequestResponse> getAllRequestsForHr() {

        return assetRequestRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /*
     * HR starts reviewing a submitted request.
     *
     * Status movement:
     * REQUEST_SUBMITTED -> UNDER_HR_REVIEW
     */
    public AssetRequestResponse startHrReview(
            int requestId,
            HrReviewRequest reviewRequest,
            String hrEmail
    ) {

        /*
         * Find the HR/Admin user who is performing this action.
         */
        User hrUser = userRepository
                .findByEmail(hrEmail)
                .orElseThrow(() -> new RuntimeException("HR user not found"));

        /*
         * Find the asset request.
         */
        AssetRequest assetRequest = assetRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new RuntimeException("Asset request not found"));

        /*
         * HR can only start review for newly submitted requests.
         */
        if (assetRequest.getStatus() != AssetRequestStatus.REQUEST_SUBMITTED) {
            throw new RuntimeException("Only submitted requests can be moved to HR review");
        }

        /*
         * Save old status before changing it.
         */
        AssetRequestStatus oldStatus = assetRequest.getStatus();

        /*
         * Update request status and HR remarks.
         */
        assetRequest.setStatus(AssetRequestStatus.UNDER_HR_REVIEW);
        assetRequest.setHrRemarks(reviewRequest.getHrRemarks());
        assetRequest.setUpdatedAt(LocalDateTime.now());

        AssetRequest savedRequest = assetRequestRepository.save(assetRequest);

        /*
         * Record timeline entry for HR review.
         */
        historyService.recordStatusChange(
                savedRequest,
                oldStatus,
                AssetRequestStatus.UNDER_HR_REVIEW,
                hrUser,
                "HR Review Started",
                reviewRequest.getHrRemarks()
        );

        return mapToResponse(savedRequest);
    }

    /*
     * HR sends request to final approver.
     *
     * Status movement:
     * DEALER_QUOTATION_RECEIVED -> SENT_FOR_FINAL_APPROVAL
     */
    public AssetRequestResponse sendForFinalApproval(int requestId, String hrEmail) {

        /*
         * Find HR/Admin user who is sending the request.
         */
        User hrUser = userRepository
                .findByEmail(hrEmail)
                .orElseThrow(() -> new RuntimeException("HR user not found"));

        /*
         * Find asset request.
         */
        AssetRequest assetRequest = assetRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new RuntimeException("Asset request not found"));

        /*
         * Request can be sent for final approval only after dealer quotation.
         */
        if (assetRequest.getStatus() != AssetRequestStatus.DEALER_QUOTATION_RECEIVED) {
            throw new RuntimeException("Only requests with dealer quotation can be sent for final approval");
        }

        /*
         * Extra safety check: dealer quotation must exist.
         */
        if (!dealerQuotationRepository.existsByAssetRequestId(requestId)) {
            throw new RuntimeException("Dealer quotation is required before final approval");
        }

        AssetRequestStatus oldStatus = assetRequest.getStatus();

        assetRequest.setStatus(AssetRequestStatus.SENT_FOR_FINAL_APPROVAL);
        assetRequest.setUpdatedAt(LocalDateTime.now());

        AssetRequest savedRequest = assetRequestRepository.save(assetRequest);

        /*
         * Record timeline entry for sending to final approver.
         */
        historyService.recordStatusChange(
                savedRequest,
                oldStatus,
                AssetRequestStatus.SENT_FOR_FINAL_APPROVAL,
                hrUser,
                "Sent For Final Approval",
                "HR sent request to final approver"
        );

        return mapToResponse(savedRequest);
    }

    /*
     * Converts AssetRequest entity into AssetRequestResponse DTO.
     */
    private AssetRequestResponse mapToResponse(AssetRequest request) {

        return new AssetRequestResponse(
                request.getId(),
                request.getAssetName(),
                request.getQuantity(),
                request.getReason(),
                request.getPriority().name(),
                request.getNeededByDate(),
                request.getStatus().name(),
                request.getCreatedBy().getName(),
                request.getCreatedBy().getEmail(),
                request.getCreatedAt(),
                request.getHrRemarks()
        );
    }
    /*
     * HR/Admin can view requests that were finally approved.
     */
    public List<AssetRequestResponse> getFinalApprovedRequests() {

        return assetRequestRepository
                .findByStatusOrderByUpdatedAtDesc(AssetRequestStatus.FINAL_APPROVED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    /*
     * HR/Admin can view requests that were finally rejected.
     */
    public List<AssetRequestResponse> getFinalRejectedRequests() {

        return assetRequestRepository
                .findByStatusOrderByUpdatedAtDesc(AssetRequestStatus.FINAL_REJECTED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    /*
     * HR/Admin notifies employee after final approval or rejection.
     *
     * Allowed statuses:
     * FINAL_APPROVED
     * FINAL_REJECTED
     *
     * Status movement:
     * FINAL_APPROVED / FINAL_REJECTED -> EMPLOYEE_NOTIFIED
     */
    public AssetRequestResponse notifyEmployee(int requestId, String hrEmail) {

        User hrUser = userRepository
                .findByEmail(hrEmail)
                .orElseThrow(() -> new RuntimeException("HR user not found"));

        AssetRequest assetRequest = assetRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new RuntimeException("Asset request not found"));

        if (assetRequest.getStatus() != AssetRequestStatus.FINAL_APPROVED &&
                assetRequest.getStatus() != AssetRequestStatus.FINAL_REJECTED) {
            throw new RuntimeException("Employee can be notified only after final approval or rejection");
        }

        FinalApproval finalApproval = finalApprovalRepository
                .findByAssetRequestId(requestId)
                .orElseThrow(() -> new RuntimeException("Final approval record not found"));

        DealerQuotation quotation = dealerQuotationRepository
                .findByAssetRequestId(requestId)
                .orElse(null);

        AssetRequestStatus oldStatus = assetRequest.getStatus();

        emailService.sendFinalDecisionEmail(
                assetRequest.getCreatedBy(),
                assetRequest,
                finalApproval,
                quotation
        );

        assetRequest.setStatus(AssetRequestStatus.EMPLOYEE_NOTIFIED);
        assetRequest.setUpdatedAt(LocalDateTime.now());

        AssetRequest savedRequest = assetRequestRepository.save(assetRequest);

        historyService.recordStatusChange(
                savedRequest,
                oldStatus,
                AssetRequestStatus.EMPLOYEE_NOTIFIED,
                hrUser,
                "Employee Notified",
                "Employee was notified by email about final decision"
        );

        return mapToResponse(savedRequest);
    }
    /*
     * HR/Admin sends approved order to dealer.
     *
     * Allowed statuses:
     * FINAL_APPROVED
     * EMPLOYEE_NOTIFIED
     *
     * Status movement:
     * FINAL_APPROVED / EMPLOYEE_NOTIFIED -> ORDER_SENT_TO_DEALER
     */
    public AssetRequestResponse sendOrderToDealer(int requestId, String hrEmail) {

        User hrUser = userRepository
                .findByEmail(hrEmail)
                .orElseThrow(() -> new RuntimeException("HR user not found"));

        AssetRequest assetRequest = assetRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new RuntimeException("Asset request not found"));

        if (assetRequest.getStatus() != AssetRequestStatus.FINAL_APPROVED &&
                assetRequest.getStatus() != AssetRequestStatus.EMPLOYEE_NOTIFIED) {
            throw new RuntimeException("Only approved requests can be sent to dealer");
        }

        AssetRequestStatus oldStatus = assetRequest.getStatus();

        assetRequest.setStatus(AssetRequestStatus.ORDER_SENT_TO_DEALER);
        assetRequest.setUpdatedAt(LocalDateTime.now());

        AssetRequest savedRequest = assetRequestRepository.save(assetRequest);

        historyService.recordStatusChange(
                savedRequest,
                oldStatus,
                AssetRequestStatus.ORDER_SENT_TO_DEALER,
                hrUser,
                "Order Sent To Dealer",
                "HR sent approved order to dealer"
        );

        return mapToResponse(savedRequest);
    }
    /*
     * HR/Admin marks product as delivered.
     *
     * Allowed status:
     * ORDER_SENT_TO_DEALER
     *
     * Status movement:
     * ORDER_SENT_TO_DEALER -> DELIVERED
     */
    public AssetRequestResponse markDelivered(int requestId, String hrEmail) {

        User hrUser = userRepository
                .findByEmail(hrEmail)
                .orElseThrow(() -> new RuntimeException("HR user not found"));

        AssetRequest assetRequest = assetRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new RuntimeException("Asset request not found"));

        if (assetRequest.getStatus() != AssetRequestStatus.ORDER_SENT_TO_DEALER) {
            throw new RuntimeException("Only orders sent to dealer can be marked as delivered");
        }

        AssetRequestStatus oldStatus = assetRequest.getStatus();

        assetRequest.setStatus(AssetRequestStatus.DELIVERED);
        assetRequest.setUpdatedAt(LocalDateTime.now());

        AssetRequest savedRequest = assetRequestRepository.save(assetRequest);

        historyService.recordStatusChange(
                savedRequest,
                oldStatus,
                AssetRequestStatus.DELIVERED,
                hrUser,
                "Product Delivered",
                "HR marked the product as delivered"
        );

        emailService.sendDeliveryCompletedEmail(
                savedRequest.getCreatedBy(),
                savedRequest
        );

        return mapToResponse(savedRequest);
    }
    /*
     * HR/Admin closes request after completion.
     *
     * Allowed statuses:
     * DELIVERED
     * EMPLOYEE_NOTIFIED
     *
     * Status movement:
     * DELIVERED / EMPLOYEE_NOTIFIED -> CLOSED
     */
    public AssetRequestResponse closeRequest(int requestId, String hrEmail) {

        User hrUser = userRepository
                .findByEmail(hrEmail)
                .orElseThrow(() -> new RuntimeException("HR user not found"));

        AssetRequest assetRequest = assetRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new RuntimeException("Asset request not found"));

        if (assetRequest.getStatus() != AssetRequestStatus.DELIVERED &&
                assetRequest.getStatus() != AssetRequestStatus.EMPLOYEE_NOTIFIED) {
            throw new RuntimeException("Only delivered or notified requests can be closed");
        }

        AssetRequestStatus oldStatus = assetRequest.getStatus();

        assetRequest.setStatus(AssetRequestStatus.CLOSED);
        assetRequest.setUpdatedAt(LocalDateTime.now());

        AssetRequest savedRequest = assetRequestRepository.save(assetRequest);

        historyService.recordStatusChange(
                savedRequest,
                oldStatus,
                AssetRequestStatus.CLOSED,
                hrUser,
                "Request Closed",
                "HR closed the request"
        );

        return mapToResponse(savedRequest);
    }
}