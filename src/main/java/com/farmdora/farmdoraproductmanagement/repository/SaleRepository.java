package com.farmdora.farmdoraproductmanagement.repository;

import com.farmdora.farmdoraproductmanagement.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Integer> {
    // 특정 saleId에 해당하는 판매자의 userId 조회
    @Query("SELECT s.seller.user.userId FROM Sale s WHERE s.id = :saleId")
    Integer findUserIdBySaleId(@Param("saleId") Integer saleId);

}