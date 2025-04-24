package com.farmdora.farmdoraproductmanagement.repository;

import com.farmdora.farmdoraproductmanagement.entity.SaleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SaleTypeRepository extends JpaRepository<SaleType, Integer> {
    // 필요한 커스텀 쿼리 메서드 추가
    @Query("SELECT ot FROM SaleType ot JOIN FETCH ot.saleTypeBig WHERE ot.id = :id")
    Optional<SaleType> findByIdWithTypeBig(@Param("id") Integer id);
}