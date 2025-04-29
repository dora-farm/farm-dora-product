package com.farmdora.farmdoraproduct.repository;

import com.farmdora.farmdoraproduct.dto.BroadcastMainDto;
import com.farmdora.farmdoraproduct.entity.Broadcast;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BroadcastRepository extends JpaRepository<Broadcast, Integer> {
    // Seller ID로 방송 목록 조회
    Page<Broadcast> findBySellerId(Integer sellerId, Pageable pageable);

    @Query("SELECT b FROM Broadcast b WHERE b.seller.id = :sellerId AND (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.desc) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Broadcast> searchBySellerId(
            @Param("sellerId") Integer sellerId,
            @Param("keyword") String keyword,
            Pageable pageable);

    // 키워드로 검색하는 메서드 추가
    @Query("SELECT b FROM Broadcast b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.desc) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Broadcast> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT new com.farmdora.farmdoraproduct.dto.BroadcastMainDto(" +
            "b.id, b.seller, b.title, b.content, b.desc, b.isBlind, b.createdDate) " +
            "FROM Broadcast b JOIN b.seller s WHERE b.isBlind = false")
    Page<BroadcastMainDto> findAllNotBlindedAsDto(Pageable pageable);
    
}