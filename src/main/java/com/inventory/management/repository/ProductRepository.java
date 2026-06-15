package com.inventory.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.management.entity.Product;
import com.inventory.management.enums.ProductStatus;

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
}