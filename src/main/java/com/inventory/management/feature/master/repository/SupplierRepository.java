package com.inventory.management.feature.master.repository;

import com.inventory.management.feature.master.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface SupplierRepository
        extends JpaRepository<Supplier, Long> {

    List<Supplier> findByTenant_Id(Long tenantId);

    List<Supplier> findByTenant_IdAndStatus(
            Long tenantId,
            String status);

    Optional<Supplier> findByTenant_IdAndName(
            Long tenantId,
            String name);

    Optional<Supplier> findByTenant_IdAndNameIgnoreCase(
            Long tenantId,
            String name);

    Optional<Supplier> findTopByTenant_IdOrderByIdDesc(
            Long tenantId);
}