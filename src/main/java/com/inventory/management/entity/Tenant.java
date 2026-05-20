package com.inventory.management.entity;

import jakarta.persistence.*;

import lombok.Data;

@Entity
@Data
@Table(name = "tenants")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;

    private String email;

    private String phone;

    private String status = "ACTIVE";
}