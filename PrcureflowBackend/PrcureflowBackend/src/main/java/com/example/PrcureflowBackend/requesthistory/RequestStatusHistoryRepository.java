package com.example.PrcureflowBackend.requesthistory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestStatusHistoryRepository extends JpaRepository<RequestStatusHistory, Integer> {

    List<RequestStatusHistory> findByAssetRequestIdOrderByChangedAtAsc(int assetRequestId);
}