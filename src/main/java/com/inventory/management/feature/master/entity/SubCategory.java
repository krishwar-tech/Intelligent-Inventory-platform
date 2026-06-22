package com.inventory.management.feature.master.entity;

import com.inventory.management.feature.auth.entity.Tenant;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "sub_categories")
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String status = "ACTIVE";

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}