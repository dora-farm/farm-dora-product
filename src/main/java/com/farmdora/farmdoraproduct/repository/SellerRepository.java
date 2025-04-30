package com.farmdora.farmdoraproduct.repository;

import com.farmdora.farmdoraproduct.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Integer> {
    @Query("SELECT s FROM Seller s WHERE s.user.userId = :userId")
    Optional<Seller> findSellerByUserId(@Param("userId") Integer userId);
}