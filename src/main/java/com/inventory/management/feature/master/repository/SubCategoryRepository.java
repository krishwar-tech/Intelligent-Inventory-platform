package com.inventory.management.feature.master.repository;

import com.inventory.management.feature.master.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubCategoryRepository
        extends JpaRepository<SubCategory, Long> {

    List<SubCategory> findByTenant_Id(Long tenantId);

    List<SubCategory> findByCategory_Id(Long categoryId);

    Optional<SubCategory>
    findByTenant_IdAndNameIgnoreCase(
            Long tenantId,
            String name);

    List<SubCategory> findByTenant_IdAndStatus(
            Long tenantId,
            String status);
}