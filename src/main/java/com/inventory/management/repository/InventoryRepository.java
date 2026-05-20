package com.inventory.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.management.entity.InventoryTransaction;
import com.inventory.management.entity.Product;

import java.util.List;

public interface InventoryRepository extends JpaRepository<InventoryTransaction, Long> {

	List<InventoryTransaction> findByTenant_IdAndProduct_IdOrderByIdDesc(Long tenantId, Long productId);

	List<InventoryTransaction> findByTenant_IdOrderByIdDesc(Long tenantId);

	void deleteByProduct(Product product);
}