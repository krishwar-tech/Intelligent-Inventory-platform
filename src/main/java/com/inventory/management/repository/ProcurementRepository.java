package com.inventory.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.management.entity.Procurement;

import java.time.LocalDate;
import java.util.List;

public interface ProcurementRepository extends JpaRepository<Procurement, Long> {

	List<Procurement> findByTenant_Id(Long tenantId);

	List<Procurement> findByTenant_IdAndSupplier_Id(Long tenantId, Long supplierId);

	List<Procurement> findByTenant_IdAndPaymentStatus(Long tenantId, String status);

	List<Procurement> findByTenant_IdAndDateBetween(Long tenantId, LocalDate start, LocalDate end);

	List<Procurement> findByTenant_IdAndProduct_Id(Long tenantId, Long id);

	void deleteByProduct_Id(Long productId);

	
}