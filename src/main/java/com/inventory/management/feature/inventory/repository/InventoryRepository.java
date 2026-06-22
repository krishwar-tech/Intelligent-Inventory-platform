package com.inventory.management.feature.inventory.repository;

import com.inventory.management.feature.inventory.entity.InventoryTransaction;
import com.inventory.management.feature.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface InventoryRepository extends JpaRepository<InventoryTransaction, Long> {

	List<InventoryTransaction> findByTenant_IdAndProduct_IdOrderByIdDesc(Long tenantId, Long productId);

	List<InventoryTransaction> findByTenant_IdOrderByIdDesc(Long tenantId);

	void deleteByProduct(Product product);
}