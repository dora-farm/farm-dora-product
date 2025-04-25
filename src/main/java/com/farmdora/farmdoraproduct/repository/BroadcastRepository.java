package com.farmdora.farmdoraproduct.repository;

import com.farmdora.farmdoraproduct.entity.Broadcast;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BroadcastRepository extends JpaRepository<Broadcast, Integer> {
    // Seller ID로 방송 목록 조회
    Page<Broadcast> findBySellerId(Integer sellerId, Pageable pageable);
}