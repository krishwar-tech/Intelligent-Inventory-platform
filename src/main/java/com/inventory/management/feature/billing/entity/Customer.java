package com.inventory.management.feature.billing.entity;

import com.inventory.management.feature.auth.entity.Tenant;
import jakarta.persistence.*;
import lombok.Data;
@Entity
@Data
@Table(name = "customer")
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String customerCode;

	private String name;

	private String status = "ACTIVE";

	@ManyToOne
	@JoinColumn(name = "tenant_id")
	private Tenant tenant;
}