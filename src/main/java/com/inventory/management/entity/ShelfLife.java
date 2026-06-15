package com.inventory.management.entity;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Data
@Table(name = "shelf_life_master")
public class ShelfLife {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer months;
    private String status = "ACTIVE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;

    @Column(name = "mapping_names")
    private String mappingNames; // e.g. "Herbal Soaps, Skincare Soap"
}