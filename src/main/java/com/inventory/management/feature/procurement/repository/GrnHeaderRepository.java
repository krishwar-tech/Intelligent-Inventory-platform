package com.inventory.management.feature.procurement.repository;

import com.inventory.management.feature.procurement.entity.GrnHeader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GrnHeaderRepository
        extends JpaRepository<GrnHeader, Long> {

    List<GrnHeader> findByTenant_Id(Long tenantId);

    Optional<GrnHeader>
    findByGrnNumber(String grnNumber);

    Optional<GrnHeader>
    findTopByTenant_IdOrderByIdDesc(Long tenantId);
}
