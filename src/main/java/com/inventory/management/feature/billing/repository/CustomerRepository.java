package com.inventory.management.feature.billing.repository;

import com.inventory.management.feature.billing.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByTenant_Id(Long tenantId);

    List<Customer> findByTenant_IdAndStatus(
            Long tenantId,
            String status
    );
    Optional<Customer> findTopByTenant_IdOrderByIdDesc(
            Long tenantId);
}