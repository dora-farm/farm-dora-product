package com.farmdora.farmdoraproductmanagement.repository;

import com.farmdora.farmdoraproductmanagement.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Integer> {
    // 필요한 커스텀 쿼리 메서드 추가
}