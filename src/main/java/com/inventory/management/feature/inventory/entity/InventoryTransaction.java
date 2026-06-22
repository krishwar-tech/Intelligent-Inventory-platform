package com.inventory.management.feature.inventory.entity;

import java.time.LocalDateTime;

import com.inventory.management.common.enums.InventoryActionType;
import com.inventory.management.feature.auth.entity.Tenant;
import com.inventory.management.feature.product.entity.Product;
import jakarta.persistence.*;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Data
@Table(name = "inventory_transactions")
public class InventoryTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private InventoryActionType type;

	private Integer qty;

	private Integer balanceStock;

	private String remarks;

	private LocalDateTime createdAt;

	@ManyToOne
	@JoinColumn(name = "product_id")
	@JsonIgnoreProperties({ "category" })
	private Product product;

	@ManyToOne
	@JoinColumn(name = "tenant_id")
	private Tenant tenant;
}