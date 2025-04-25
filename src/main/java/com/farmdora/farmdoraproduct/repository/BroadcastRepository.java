package com.farmdora.farmdoraproduct.repository;

import com.farmdora.farmdoraproduct.entity.Broadcast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BroadcastRepository extends JpaRepository<Broadcast, Integer> {

}