package com.example.PrcureflowBackend.finalapproval;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FinalApprovalRepository extends JpaRepository<FinalApproval, Integer> {

    boolean existsByAssetRequestId(int assetRequestId);

    Optional<FinalApproval> findByAssetRequestId(int assetRequestId);
}