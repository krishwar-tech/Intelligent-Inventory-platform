package com.inventory.management.service;

import com.inventory.management.entity.Product;
import com.inventory.management.enums.InventoryActionType;

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