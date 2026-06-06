package com.example.PrcureflowBackend.dealerquotation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.PrcureflowBackend.dealerquotation.dto.DealerQuotationRequest;
import com.example.PrcureflowBackend.dealerquotation.dto.DealerQuotationResponse;

/*
 * DealerQuotationController exposes dealer quotation APIs.
 *
 * In V1:
 * - Dealer does not login.
 * - HR_MANAGER or ADMIN manually enters quotation.
 */
@RestController
@RequestMapping("/api/dealer-quotations")
public class DealerQuotationController {

    private final DealerQuotationService dealerQuotationService;

    /*
     * Constructor injection.
     */
    public DealerQuotationController(DealerQuotationService dealerQuotationService) {
        this.dealerQuotationService = dealerQuotationService;
    }

    /*
     * HR/Admin adds dealer quotation for an asset request.
     *
     * API:
     * POST /api/dealer-quotations/asset-requests/{assetRequestId}
     *
     * Required role:
     * HR_MANAGER or ADMIN
     *
     * Authentication is used to get the logged-in HR/Admin email.
     * This is needed for request timeline/history.
     */
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('ADMIN')")
    @PostMapping("/asset-requests/{assetRequestId}")
    public ResponseEntity<DealerQuotationResponse> addQuotation(
            @PathVariable int assetRequestId,
            @RequestBody DealerQuotationRequest request,
            Authentication authentication
    ) {
        String hrEmail = authentication.getName();

        return ResponseEntity.ok(
                dealerQuotationService.addQuotation(assetRequestId, request, hrEmail)
        );
    }

    /*
     * HR/Admin fetches quotation for an asset request.
     *
     * API:
     * GET /api/dealer-quotations/asset-requests/{assetRequestId}
     */
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('ADMIN')")
    @GetMapping("/asset-requests/{assetRequestId}")
    public ResponseEntity<DealerQuotationResponse> getQuotationByAssetRequest(
            @PathVariable int assetRequestId
    ) {
        return ResponseEntity.ok(
                dealerQuotationService.getQuotationByAssetRequest(assetRequestId)
        );
    }
}