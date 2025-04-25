package com.farmdora.farmdoraproduct.repository;

import com.farmdora.farmdoraproduct.entity.Sale;
import com.farmdora.farmdoraproduct.entity.SaleFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleFileRepository extends JpaRepository<SaleFile, Integer> {
    List<SaleFile> findBySale(Sale sale);
    // 필요한 커스텀 쿼리 메서드 추가
}