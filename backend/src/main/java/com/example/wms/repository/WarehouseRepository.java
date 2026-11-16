package com.example.wms.repository;

import com.example.wms.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, JpaSpecificationExecutor<Warehouse> {

    boolean existsByCode(String code);

    Optional<Warehouse> findByCode(String code);
}
