package com.inventory.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.management.entity.Procurement;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProcurementRepository extends JpaRepository<Procurement, Long> {

    List<Procurement> findByTenant_Id(Long tenantId);

    List<Procurement> findByTenant_IdAndSupplier_Id(Long tenantId, Long supplierId);

    List<Procurement> findByTenant_IdAndPaymentStatus(Long tenantId, String status);

    List<Procurement> findByTenant_IdAndDateBetween(Long tenantId, LocalDate start, LocalDate end);

    List<Procurement> findByTenant_IdAndProduct_Id(Long tenantId, Long id);

    void deleteByProduct_Id(Long productId);

    Optional<Procurement>
    findTopByTenant_IdOrderByIdDesc(Long tenantId);

    List<Procurement>
    findByTenant_IdAndGrnNumber(
            Long tenantId,
            String grnNumber);

    List<Procurement>
    findByTenant_IdAndGrnNumberIsNotNull(
            Long tenantId);

    Optional<Procurement>
    findTopByTenant_IdAndGrnNumberIsNotNullOrderByIdDesc(
            Long tenantId);

    void deleteByTenant_IdAndGrnNumber(
            Long tenantId,
            String grnNumber);

    long countByTenant_IdAndGrnNumber(
            Long tenantId,
            String grnNumber);


}