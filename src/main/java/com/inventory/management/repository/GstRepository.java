package com.inventory.management.repository;

import com.inventory.management.entity.GstMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GstRepository
        extends JpaRepository<GstMaster, Long> {

    List<GstMaster> findByTenant_IdAndStatus(
            Long tenantId,
            String status);

    Optional<GstMaster> findBySubCategory_Id(
            Long subCategoryId);

    Optional<GstMaster> findByCategory_Id(
            Long categoryId);
}