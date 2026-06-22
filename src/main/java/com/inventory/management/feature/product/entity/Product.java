package com.inventory.management.feature.product.entity;

import com.inventory.management.common.enums.ProductStatus;
import com.inventory.management.feature.auth.entity.Tenant;
import com.inventory.management.feature.master.entity.Category;
import com.inventory.management.feature.master.entity.SubCategory;
import jakarta.persistence.*;

import lombok.Data;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Data
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String barcode;

    private String sku;

    private Integer stock = 0;

    @Column(name = "pack_size")
    private Integer packSize = 0;

    private Integer reorderLevel = 10;

    private Integer safetyStock = 5;

    private BigDecimal price = BigDecimal.ZERO;

    private BigDecimal mrp = BigDecimal.ZERO;

    private String unit = "pcs";

    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties("products")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @ManyToOne
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;
}