package com.inventory.management.feature.auth.repository;


import com.inventory.management.feature.auth.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository
        extends JpaRepository<Tenant, Long> {
}