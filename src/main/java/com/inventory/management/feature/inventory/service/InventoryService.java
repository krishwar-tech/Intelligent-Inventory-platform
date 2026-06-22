package com.inventory.management.feature.inventory.service;


import com.inventory.management.common.enums.InventoryActionType;
import com.inventory.management.feature.product.entity.Product;

public interface InventoryService {

    void increaseStock(
            Product product,
            int qty,
            InventoryActionType type,
            String remarks
    );

    void decreaseStock(
            Product product,
            int qty,
            InventoryActionType type,
            String remarks
    );
}