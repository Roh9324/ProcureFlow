package com.example.PrcureflowBackend.dealerquotation;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DealerQuotationRepository extends JpaRepository<DealerQuotation, Integer> {

    Optional<DealerQuotation> findByAssetRequestId(int assetRequestId);

    boolean existsByAssetRequestId(int assetRequestId);
}