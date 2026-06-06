package com.example.PrcureflowBackend.assetrequest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRequestRepository extends JpaRepository<AssetRequest, Integer> {

    List<AssetRequest> findByCreatedByEmailOrderByCreatedAtDesc(String email);

    List<AssetRequest> findAllByOrderByCreatedAtDesc();

    List<AssetRequest> findByStatusOrderByUpdatedAtDesc(AssetRequestStatus status);
}