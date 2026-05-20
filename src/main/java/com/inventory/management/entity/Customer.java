package com.inventory.management.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "customer")
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String status = "ACTIVE";

	@ManyToOne
	@JoinColumn(name = "tenant_id")
	private Tenant tenant;
}