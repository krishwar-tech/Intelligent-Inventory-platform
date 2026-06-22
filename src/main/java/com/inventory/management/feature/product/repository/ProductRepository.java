package com.inventory.management.feature.product.repository;

import com.inventory.management.common.enums.ProductStatus;
import com.inventory.management.feature.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.*;

public interface ProductRepository
		extends JpaRepository<Product, Long> {

	Optional<Product> findByBarcode(String barcode);

	Optional<Product> findByIdAndTenant_Id(
			Long id,
			Long tenantId);

	Optional<Product> findBySku(String sku);

	Optional<Product> findByNameIgnoreCase(String name);

	Optional<Product> findByName(String name);

	List<Product> findByStatus(ProductStatus status);

	List<Product> findByCategory_Id(Long categoryId);

	List<Product> findByNameContainingIgnoreCase(String name);

	List<Product> findByTenant_Id(Long tenantId);

	List<Product> findByTenant_IdAndStatus(
			Long tenantId,
			ProductStatus status);

	List<Product> findByTenant_IdAndCategory_Id(
			Long tenantId,
			Long categoryId);

	List<Product> findByTenant_IdAndNameContainingIgnoreCase(
			Long tenantId,
			String name);

	Optional<Product> findByTenant_IdAndBarcode(
			Long tenantId,
			String barcode);

	Optional<Product> findByTenant_IdAndNameIgnoreCase(
			Long tenantId,
			String name);

	Optional<Product>
	findByNameIgnoreCaseAndTenant_Id(
			String name,
			Long tenantId);

	List<Product>
	findByNameContainingIgnoreCaseAndTenant_Id(
			String name,
			Long tenantId);
}