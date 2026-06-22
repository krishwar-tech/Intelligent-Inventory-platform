package com.inventory.management.feature.billing.repository;

import com.inventory.management.feature.billing.entity.SaleItem;
import com.inventory.management.feature.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    List<SaleItem> findBySale_Id(Long saleId);

    List<SaleItem> findByProduct_Id(Long productId);
    
    
    void deleteByProduct(Product product);
}