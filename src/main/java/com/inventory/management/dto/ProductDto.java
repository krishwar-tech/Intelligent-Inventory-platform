package com.inventory.management.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductDto {

    private Long id;

    private String name;

    private String barcode;

    private String sku;

    private Integer stock;

    private Integer reorderLevel;

    private Integer safetyStock;

    private BigDecimal price;

    private BigDecimal mrp;

    private String unit;

    private String status;

    private String categoryName;
}