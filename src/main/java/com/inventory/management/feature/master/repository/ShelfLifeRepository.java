package com.inventory.management.feature.master.repository;

import com.inventory.management.feature.master.entity.ShelfLife;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
public interface ShelfLifeRepository
        extends JpaRepository<ShelfLife, Long> {

    List<ShelfLife> findByTenant_IdAndStatus(
            Long tenantId,
            String status
    );

    Optional<ShelfLife> findBySubCategory_Id(
            Long subCategoryId);

    Optional<ShelfLife> findByCategory_Id(
            Long categoryId);
}