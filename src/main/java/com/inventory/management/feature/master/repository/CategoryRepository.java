package com.inventory.management.feature.master.repository;

import com.inventory.management.feature.master.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	List<Category> findByStatus(String status);

	Optional<Category> findByNameIgnoreCase(String name);

	List<Category> findByTenant_Id(Long tenantId);

	List<Category> findByTenant_IdAndStatus(Long tenantId, String status);

	Optional<Category> findByTenant_IdAndNameIgnoreCase(Long tenantId, String name);
}