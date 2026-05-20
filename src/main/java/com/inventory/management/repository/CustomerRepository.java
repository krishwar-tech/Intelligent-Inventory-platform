package com.inventory.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.management.entity.Customer;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByTenant_Id(Long tenantId);

    List<Customer> findByTenant_IdAndStatus(
            Long tenantId,
            String status
    );
}