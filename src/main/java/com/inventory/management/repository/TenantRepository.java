package com.inventory.management.repository;

import com.inventory.management.entity.Tenant;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository
        extends JpaRepository<Tenant, Long> {
}