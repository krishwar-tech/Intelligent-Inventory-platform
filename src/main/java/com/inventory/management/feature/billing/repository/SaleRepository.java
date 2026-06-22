package com.inventory.management.feature.billing.repository;

import com.inventory.management.feature.billing.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    Optional<Sale> findByBillNo(String billNo);

    List<Sale> findByTenant_Id(Long tenantId);
}