package com.inventory.management.feature.master.entity;

import com.inventory.management.feature.auth.entity.Tenant;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String supplierCode;

    @Column(unique = true)
    private String name;

    private String status = "ACTIVE";

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}