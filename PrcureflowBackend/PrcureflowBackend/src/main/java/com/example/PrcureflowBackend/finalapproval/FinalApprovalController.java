package com.example.PrcureflowBackend.finalapproval;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.PrcureflowBackend.finalapproval.dto.FinalApprovalDecisionRequest;
import com.example.PrcureflowBackend.finalapproval.dto.FinalApprovalResponse;
import com.example.PrcureflowBackend.finalapproval.dto.PendingFinalApprovalResponse;

@RestController
@RequestMapping("/api/final-approvals")
public class FinalApprovalController {

    private final FinalApprovalService finalApprovalService;

    public FinalApprovalController(FinalApprovalService finalApprovalService) {
        this.finalApprovalService = finalApprovalService;
    }

    /*
     * Final approver views pending approvals.
     *
     * API:
     * GET /api/final-approvals/pending
     */
    @PreAuthorize("hasRole('FINAL_APPROVER') or hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<PendingFinalApprovalResponse>> getPendingApprovals() {
        return ResponseEntity.ok(
                finalApprovalService.getPendingApprovals()
        );
    }

    /*
     * Final approver approves/rejects a request.
     *
     * API:
     * PUT /api/final-approvals/{assetRequestId}/decision
     */
    @PreAuthorize("hasRole('FINAL_APPROVER') or hasRole('ADMIN')")
    @PutMapping("/{assetRequestId}/decision")
    public ResponseEntity<FinalApprovalResponse> decide(
            @PathVariable int assetRequestId,
            @RequestBody FinalApprovalDecisionRequest request,
            Authentication authentication
    ) {
        String approverEmail = authentication.getName();

        return ResponseEntity.ok(
                finalApprovalService.decide(assetRequestId, request, approverEmail)
        );
    }
}