package com.inventory.management.service;

import com.inventory.management.entity.InventoryTransaction;
import com.inventory.management.entity.Product;

import com.inventory.management.repository.InventoryRepository;
import com.inventory.management.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.management.entity.Tenant;
import com.inventory.management.repository.TenantRepository;
import com.inventory.management.config.TenantContext;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.inventory.management.exception.*;

@Service
public class InventoryService {

	private final ProductRepository productRepo;

	private final InventoryRepository inventoryRepo;
	private final TenantRepository tenantRepo;

	private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

	public InventoryService(ProductRepository productRepo, InventoryRepository inventoryRepo,
			TenantRepository tenantRepo) {

		this.productRepo = productRepo;

		this.inventoryRepo = inventoryRepo;

		this.tenantRepo = tenantRepo;
	}

	// Stock Increase

	@Transactional
	public void increaseStock(Product product, int qty, String type, String remarks) {

		log.info("Stock increase started for : {}", product.getName());

		int stock = product.getStock() == null ? 0 : product.getStock();

		int previousStock = stock;

		stock += qty;

		product.setStock(stock);

		productRepo.save(product);

		log.info("Stock increased for {} | Previous : {} | Added : {} | Current : {}", product.getName(), previousStock,
				qty, stock);

		saveTxn(product, type, qty, stock, remarks);

		if (stock <= product.getReorderLevel()) {

			log.warn("Low stock warning for {} | Remaining : {}", product.getName(), stock);
		}

		log.info("Stock increase completed for : {}", product.getName());
	}

	// Stock decrease

	@Transactional
	public void decreaseStock(Product product, int qty, String type, String remarks) {

		log.info("Stock decrease started for : {}", product.getName());

		int stock = product.getStock() == null ? 0 : product.getStock();

		int previousStock = stock;

		if (qty > stock) {

			log.error("Insufficient stock for {} | Available : {} | Requested : {}", product.getName(), stock, qty);

			throw new InsufficientStockException(product.getName() + " insufficient stock");
		}

		stock -= qty;

		product.setStock(stock);

		productRepo.save(product);

		log.info("Stock reduced for {} | Previous : {} | Reduced : {} | Remaining : {}", product.getName(),
				previousStock, qty, stock);

		saveTxn(product, type, qty, stock, remarks);

		if (stock <= product.getReorderLevel()) {

			log.warn("Low stock warning for {} | Remaining : {}", product.getName(), stock);
		}

		if (stock <= 2) {

			log.error("Critical stock level reached for {}", product.getName());
		}

		log.info("Stock decrease completed for : {}", product.getName());
	}

	// Save Transaction

	private void saveTxn(Product product, String type, int qty, int balance, String remarks) {

		log.info("Saving inventory transaction for : {}", product.getName());

		InventoryTransaction t = new InventoryTransaction();

		t.setProduct(product);

		t.setType(type);

		t.setQty(qty);

		t.setBalanceStock(balance);

		t.setRemarks(remarks);

		t.setCreatedAt(LocalDateTime.now());

		Tenant tenant = tenantRepo.findById(TenantContext.getTenantId()).orElseThrow();

		t.setTenant(tenant);

		inventoryRepo.save(t);

		log.info("Inventory transaction saved | Product : {} | Type : {} | Qty : {}", product.getName(), type, qty);
	}
}