package com.example.PrcureflowBackend.assetrequest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.PrcureflowBackend.assetrequest.dto.AssetRequestResponse;
import com.example.PrcureflowBackend.assetrequest.dto.CreateAssetRequest;
import com.example.PrcureflowBackend.assetrequest.dto.HrReviewRequest;
import com.example.PrcureflowBackend.requesthistory.RequestStatusHistoryService;
import com.example.PrcureflowBackend.requesthistory.dto.RequestStatusHistoryResponse;

/*
 * AssetRequestController handles all APIs related to asset requests.
 *
 * It supports:
 * 1. Employee creating asset requests
 * 2. Employee viewing their own requests
 * 3. HR/Admin viewing all requests
 * 4. HR/Admin starting HR review
 * 5. HR/Admin sending request for final approval
 * 6. Viewing request timeline/history
 */
@RestController
@RequestMapping("/api/asset-requests")
public class AssetRequestController {

    private final AssetRequestService assetRequestService;
    private final RequestStatusHistoryService historyService;

    /*
     * Constructor injection.
     *
     * Spring automatically injects:
     * - AssetRequestService
     * - RequestStatusHistoryService
     */
    public AssetRequestController(
            AssetRequestService assetRequestService,
            RequestStatusHistoryService historyService
    ) {
        this.assetRequestService = assetRequestService;
        this.historyService = historyService;
    }

    /*
     * EMPLOYEE creates a new asset request.
     *
     * API:
     * POST /api/asset-requests
     *
     * Required role:
     * EMPLOYEE
     *
     * The frontend sends only asset request details.
     * It does NOT send userId.
     *
     * The logged-in user's email is taken from the JWT token
     * using the Authentication object.
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping
    public ResponseEntity<AssetRequestResponse> createAssetRequest(
            @RequestBody CreateAssetRequest request,
            Authentication authentication
    ) {
        /*
         * authentication.getName() returns the logged-in user's email.
         *
         * This was set inside JwtAuthenticationFilter.
         */
        String userEmail = authentication.getName();

        AssetRequestResponse response =
                assetRequestService.createRequest(request, userEmail);

        return ResponseEntity.ok(response);
    }

    /*
     * EMPLOYEE views their own asset requests.
     *
     * API:
     * GET /api/asset-requests/my
     *
     * Required role:
     * EMPLOYEE
     *
     * This endpoint does not show requests created by other employees.
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my")
    public ResponseEntity<List<AssetRequestResponse>> getMyRequests(
            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        List<AssetRequestResponse> response =
                assetRequestService.getMyRequests(userEmail);

        return ResponseEntity.ok(response);
    }

    /*
     * HR_MANAGER or ADMIN views all employee asset requests.
     *
     * API:
     * GET /api/asset-requests/all
     *
     * Required role:
     * HR_MANAGER or ADMIN
     *
     * This is mainly used by the HR dashboard.
     */
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<AssetRequestResponse>> getAllRequestsForHr() {

        List<AssetRequestResponse> response =
                assetRequestService.getAllRequestsForHr();

        return ResponseEntity.ok(response);
    }

    /*
     * HR_MANAGER or ADMIN starts HR review for a request.
     *
     * API:
     * PUT /api/asset-requests/{requestId}/start-review
     *
     * Required role:
     * HR_MANAGER or ADMIN
     *
     * Example body:
     * {
     *   "hrRemarks": "Request is valid. Moving to HR review."
     * }
     *
     * Status movement:
     * REQUEST_SUBMITTED -> UNDER_HR_REVIEW
     *
     * The HR/Admin user's email is taken from Authentication
     * so we can record who performed this action in request history.
     */
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('ADMIN')")
    @PutMapping("/{requestId}/start-review")
    public ResponseEntity<AssetRequestResponse> startHrReview(
            @PathVariable int requestId,
            @RequestBody HrReviewRequest request,
            Authentication authentication
    ) {
        String hrEmail = authentication.getName();

        AssetRequestResponse response =
                assetRequestService.startHrReview(requestId, request, hrEmail);

        return ResponseEntity.ok(response);
    }

    /*
     * HR_MANAGER or ADMIN sends a request to the final approver.
     *
     * API:
     * PUT /api/asset-requests/{requestId}/send-for-final-approval
     *
     * Required role:
     * HR_MANAGER or ADMIN
     *
     * Allowed status before this:
     * DEALER_QUOTATION_RECEIVED
     *
     * Status movement:
     * DEALER_QUOTATION_RECEIVED -> SENT_FOR_FINAL_APPROVAL
     *
     * The HR/Admin user's email is passed to service so that
     * the timeline can record who sent the request for final approval.
     */
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('ADMIN')")
    @PutMapping("/{requestId}/send-for-final-approval")
    public ResponseEntity<AssetRequestResponse> sendForFinalApproval(
            @PathVariable int requestId,
            Authentication authentication
    ) {
        String hrEmail = authentication.getName();

        AssetRequestResponse response =
                assetRequestService.sendForFinalApproval(requestId, hrEmail);

        return ResponseEntity.ok(response);
    }

    /*
     * View full timeline/history for a specific asset request.
     *
     * API:
     * GET /api/asset-requests/{requestId}/history
     *
     * Allowed roles:
     * EMPLOYEE
     * HR_MANAGER
     * FINAL_APPROVER
     * ADMIN
     *
     * Security logic:
     * - EMPLOYEE can view only their own request history.
     * - HR_MANAGER, FINAL_APPROVER, and ADMIN can view request histories.
     *
     * This logic is handled inside RequestStatusHistoryService.
     */
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR_MANAGER') or hasRole('FINAL_APPROVER') or hasRole('ADMIN')")
    @GetMapping("/{requestId}/history")
    public ResponseEntity<List<RequestStatusHistoryResponse>> getRequestHistory(
            @PathVariable int requestId,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        List<RequestStatusHistoryResponse> response =
                historyService.getHistoryForRequest(requestId, userEmail);

        return ResponseEntity.ok(response);
    }
    /*
     * HR/Admin views finally approved requests.
     */
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('ADMIN')")
    @GetMapping("/final-approved")
    public ResponseEntity<List<AssetRequestResponse>> getFinalApprovedRequests() {
        return ResponseEntity.ok(
                assetRequestService.getFinalApprovedRequests()
        );
    }
    /*
     * HR/Admin views finally rejected requests.
     */
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('ADMIN')")
    @GetMapping("/final-rejected")
    public ResponseEntity<List<AssetRequestResponse>> getFinalRejectedRequests() {
        return ResponseEntity.ok(
                assetRequestService.getFinalRejectedRequests()
        );
    }
    /*
     * HR/Admin notifies employee after final decision.
     */
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('ADMIN')")
    @PutMapping("/{requestId}/notify-employee")
    public ResponseEntity<AssetRequestResponse> notifyEmployee(
            @PathVariable int requestId,
            Authentication authentication
    ) {
        String hrEmail = authentication.getName();

        return ResponseEntity.ok(
                assetRequestService.notifyEmployee(requestId, hrEmail)
        );
    }
    /*
     * HR/Admin sends approved order to dealer.
     */
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('ADMIN')")
    @PutMapping("/{requestId}/send-order-to-dealer")
    public ResponseEntity<AssetRequestResponse> sendOrderToDealer(
            @PathVariable int requestId,
            Authentication authentication
    ) {
        String hrEmail = authentication.getName();

        return ResponseEntity.ok(
                assetRequestService.sendOrderToDealer(requestId, hrEmail)
        );
    }
    /*
     * HR/Admin marks the asset as delivered.
     */
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('ADMIN')")
    @PutMapping("/{requestId}/mark-delivered")
    public ResponseEntity<AssetRequestResponse> markDelivered(
            @PathVariable int requestId,
            Authentication authentication
    ) {
        String hrEmail = authentication.getName();

        return ResponseEntity.ok(
                assetRequestService.markDelivered(requestId, hrEmail)
        );
    }
    /*
     * HR/Admin closes the request.
     */
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('ADMIN')")
    @PutMapping("/{requestId}/close")
    public ResponseEntity<AssetRequestResponse> closeRequest(
            @PathVariable int requestId,
            Authentication authentication
    ) {
        String hrEmail = authentication.getName();

        return ResponseEntity.ok(
                assetRequestService.closeRequest(requestId, hrEmail)
        );
    }
}