package com.example.PrcureflowBackend.dealerquotation;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.PrcureflowBackend.assetrequest.AssetRequest;
import com.example.PrcureflowBackend.assetrequest.AssetRequestRepository;
import com.example.PrcureflowBackend.assetrequest.AssetRequestStatus;
import com.example.PrcureflowBackend.dealerquotation.dto.DealerQuotationRequest;
import com.example.PrcureflowBackend.dealerquotation.dto.DealerQuotationResponse;
import com.example.PrcureflowBackend.requesthistory.RequestStatusHistoryService;
import com.example.PrcureflowBackend.user.User;
import com.example.PrcureflowBackend.user.UserRepository;

/*
 * DealerQuotationService contains business logic for dealer quotation.
 *
 * In V1:
 * - Dealer does not login.
 * - HR/Admin manually enters dealer quotation.
 */
@Service
public class DealerQuotationService {

    private final DealerQuotationRepository dealerQuotationRepository;
    private final AssetRequestRepository assetRequestRepository;
    private final RequestStatusHistoryService historyService;
    private final UserRepository userRepository;

    /*
     * Constructor injection.
     *
     * Spring injects:
     * - DealerQuotationRepository
     * - AssetRequestRepository
     * - RequestStatusHistoryService
     * - UserRepository
     */
    public DealerQuotationService(
            DealerQuotationRepository dealerQuotationRepository,
            AssetRequestRepository assetRequestRepository,
            RequestStatusHistoryService historyService,
            UserRepository userRepository
    ) {
        this.dealerQuotationRepository = dealerQuotationRepository;
        this.assetRequestRepository = assetRequestRepository;
        this.historyService = historyService;
        this.userRepository = userRepository;
    }

    /*
     * HR/Admin enters dealer quotation for an asset request.
     *
     * Required previous status:
     * UNDER_HR_REVIEW
     *
     * Status movement:
     * UNDER_HR_REVIEW -> DEALER_QUOTATION_RECEIVED
     */
    public DealerQuotationResponse addQuotation(
            int assetRequestId,
            DealerQuotationRequest request,
            String hrEmail
    ) {
        /*
         * Find HR/Admin user who is adding the quotation.
         * This is needed for request timeline/history.
         */
        User hrUser = userRepository
                .findByEmail(hrEmail)
                .orElseThrow(() -> new RuntimeException("HR user not found"));

        /*
         * Find the asset request.
         */
        AssetRequest assetRequest = assetRequestRepository
                .findById(assetRequestId)
                .orElseThrow(() -> new RuntimeException("Asset request not found"));

        /*
         * Dealer quotation can be added only after HR review starts.
         */
        if (assetRequest.getStatus() != AssetRequestStatus.UNDER_HR_REVIEW) {
            throw new RuntimeException("Quotation can be added only after HR review has started");
        }

        /*
         * In V1, one asset request can have only one dealer quotation.
         */
        if (dealerQuotationRepository.existsByAssetRequestId(assetRequestId)) {
            throw new RuntimeException("Dealer quotation already exists for this request");
        }

        /*
         * Store old status before changing it.
         * This will be used in timeline history.
         */
        AssetRequestStatus oldStatus = assetRequest.getStatus();

        /*
         * Create dealer quotation entity.
         */
        DealerQuotation quotation = new DealerQuotation();

        quotation.setAssetRequest(assetRequest);
        quotation.setDealerName(request.getDealerName());
        quotation.setQuotedPrice(request.getQuotedPrice());
        quotation.setDeliveryDays(request.getDeliveryDays());
        quotation.setWarrantyDetails(request.getWarrantyDetails());
        quotation.setDealerRemarks(request.getDealerRemarks());
        quotation.setCreatedAt(LocalDateTime.now());
        quotation.setUpdatedAt(LocalDateTime.now());

        /*
         * Save quotation in database.
         */
        DealerQuotation savedQuotation =
                dealerQuotationRepository.save(quotation);

        /*
         * After quotation is saved, update request status.
         */
        assetRequest.setStatus(AssetRequestStatus.DEALER_QUOTATION_RECEIVED);
        assetRequest.setUpdatedAt(LocalDateTime.now());

        assetRequestRepository.save(assetRequest);

        /*
         * Record timeline/history entry.
         *
         * This allows employee to see:
         * Dealer quotation received from ABC Computers.
         */
        historyService.recordStatusChange(
                assetRequest,
                oldStatus,
                AssetRequestStatus.DEALER_QUOTATION_RECEIVED,
                hrUser,
                "Dealer Quotation Received",
                "Quotation received from " + request.getDealerName()
        );

        return mapToResponse(savedQuotation);
    }

    /*
     * Fetch dealer quotation for a specific asset request.
     */
    public DealerQuotationResponse getQuotationByAssetRequest(int assetRequestId) {

        DealerQuotation quotation = dealerQuotationRepository
                .findByAssetRequestId(assetRequestId)
                .orElseThrow(() -> new RuntimeException("Dealer quotation not found"));

        return mapToResponse(quotation);
    }

    /*
     * Convert DealerQuotation entity into response DTO.
     */
    private DealerQuotationResponse mapToResponse(DealerQuotation quotation) {

        AssetRequest assetRequest = quotation.getAssetRequest();

        return new DealerQuotationResponse(
                quotation.getId(),
                assetRequest.getId(),
                assetRequest.getAssetName(),
                quotation.getDealerName(),
                quotation.getQuotedPrice(),
                quotation.getDeliveryDays(),
                quotation.getWarrantyDetails(),
                quotation.getDealerRemarks(),
                assetRequest.getStatus().name(),
                quotation.getCreatedAt()
        );
    }
}