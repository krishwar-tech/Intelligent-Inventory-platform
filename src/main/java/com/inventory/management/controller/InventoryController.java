package com.inventory.management.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventory.management.config.TenantContext;
import com.inventory.management.dto.ApiResponse;
import com.inventory.management.entity.InventoryTransaction;
import com.inventory.management.entity.Product;
import com.inventory.management.enums.InventoryActionType;
import com.inventory.management.repository.InventoryRepository;
import com.inventory.management.repository.ProductRepository;
import com.inventory.management.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

	private final InventoryService inventoryService;

	private final ProductRepository productRepo;

	private final InventoryRepository inventoryRepo;

	public InventoryController(
			InventoryService inventoryService,
			ProductRepository productRepo,
			InventoryRepository inventoryRepo) {

		this.inventoryService = inventoryService;
		this.productRepo = productRepo;
		this.inventoryRepo = inventoryRepo;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<Product>>> all() {

		Long tenantId = TenantContext.getTenantId();

		List<Product> products =
				productRepo.findByTenant_Id(tenantId);

		ApiResponse<List<Product>> response =
				new ApiResponse<>(
						true,
						"Inventory fetched successfully",
						HttpStatus.OK.value(),
						products,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/history")
	public ResponseEntity<ApiResponse<List<InventoryTransaction>>> history() {

		Long tenantId = TenantContext.getTenantId();

		List<InventoryTransaction> history =
				inventoryRepo.findByTenant_IdOrderByIdDesc(tenantId);

		ApiResponse<List<InventoryTransaction>> response =
				new ApiResponse<>(
						true,
						"Inventory history fetched successfully",
						HttpStatus.OK.value(),
						history,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/damage")
	public ResponseEntity<ApiResponse<String>> damage(
			@RequestParam Long productId,
			@RequestParam Integer qty) {

		Product p = productRepo.findById(productId).orElseThrow();

		inventoryService.decreaseStock(
				p,
				qty,
				InventoryActionType.DAMAGE,
				"Damaged");

		ApiResponse<String> response =
				new ApiResponse<>(
						true,
						"Damaged stock updated successfully",
						HttpStatus.OK.value(),
						"Stock updated",
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/return")
	public ResponseEntity<ApiResponse<String>> returns(
			@RequestParam Long productId,
			@RequestParam Integer qty) {

		Product p = productRepo.findById(productId).orElseThrow();

		inventoryService.increaseStock(
				p,
				qty,
				InventoryActionType.RETURN,
				"Returned");

		ApiResponse<String> response =
				new ApiResponse<>(
						true,
						"Returned stock updated successfully",
						HttpStatus.OK.value(),
						"Stock updated",
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/adjust")
	public ResponseEntity<ApiResponse<String>> adjust(
			@RequestParam Long productId,
			@RequestParam Integer qty,
			@RequestParam String mode,
			@RequestParam(required = false) String remarks) {

		Product p = productRepo.findById(productId).orElseThrow();

		if ("IN".equalsIgnoreCase(mode)) {

			inventoryService.increaseStock(
					p,
					qty,
					InventoryActionType.ADJUST_IN,
					remarks);

		} else {

			inventoryService.decreaseStock(
					p,
					qty,
					InventoryActionType.ADJUST_OUT,
					remarks);
		}

		ApiResponse<String> response =
				new ApiResponse<>(
						true,
						"Inventory adjusted successfully",
						HttpStatus.OK.value(),
						"Adjusted",
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}
}