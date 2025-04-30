package com.farmdora.farmdoraproduct.repository;

import com.farmdora.farmdoraproduct.entity.Option;
import com.farmdora.farmdoraproduct.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Integer> {

    List<Option> findBySale(Sale sale);
}