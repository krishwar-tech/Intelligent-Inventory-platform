package com.inventory.management.feature.master.controller;


import com.inventory.management.common.dto.ApiResponse;

import com.inventory.management.feature.billing.entity.Customer;
import com.inventory.management.feature.master.entity.*;
import com.inventory.management.feature.master.service.MasterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/masters")
public class MasterController {

	private final MasterService service;

	public MasterController(MasterService service) {
		this.service = service;
	}

	// =========================
	// CATEGORY
	// =========================

	@GetMapping("/categories")
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCategories() {

		List<Map<String, Object>> categories =
				service.getCategories();

		ApiResponse<List<Map<String, Object>>> response =
				new ApiResponse<>(
						true,
						"Categories fetched successfully",
						HttpStatus.OK.value(),
						categories,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/categories")
	public ResponseEntity<ApiResponse<Category>> addCategory(
			@RequestBody Category c) {

		Category saved = service.addCategory(c);

		ApiResponse<Category> response =
				new ApiResponse<>(
						true,
						"Category added successfully",
						HttpStatus.CREATED.value(),
						saved,
						LocalDateTime.now());

		return new ResponseEntity<>(
				response,
				HttpStatus.CREATED);
	}

	@PutMapping("/categories/{id}")
	public ResponseEntity<ApiResponse<Category>> updateCategory(
			@PathVariable Long id,
			@RequestBody Category c) {

		Category updated =
				service.updateCategory(id, c);

		ApiResponse<Category> response =
				new ApiResponse<>(
						true,
						"Category updated successfully",
						HttpStatus.OK.value(),
						updated,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/categories/{id}")
	public ResponseEntity<ApiResponse<String>> deleteCategory(
			@PathVariable Long id) {

		String result = service.deleteCategory(id);

		ApiResponse<String> response =
				new ApiResponse<>(
						true,
						"Category deleted successfully",
						HttpStatus.OK.value(),
						result,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

	// =========================
	// SUB CATEGORY
	// =========================

	@GetMapping("/subcategories")
	public ResponseEntity<ApiResponse<List<SubCategory>>> getSubCategories() {

		List<SubCategory> data =
				service.getSubCategories();

		ApiResponse<List<SubCategory>> response =
				new ApiResponse<>(
						true,
						"Sub categories fetched successfully",
						HttpStatus.OK.value(),
						data,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/subcategories")
	public ResponseEntity<ApiResponse<SubCategory>> addSubCategory(
			@RequestBody Map<String, Object> body) {

		Long categoryId =
				Long.valueOf(
						body.get("categoryId").toString());

		String name =
				body.get("name").toString();

		SubCategory saved =
				service.addSubCategory(
						categoryId,
						name);

		ApiResponse<SubCategory> response =
				new ApiResponse<>(
						true,
						"Sub category added successfully",
						HttpStatus.CREATED.value(),
						saved,
						LocalDateTime.now());

		return new ResponseEntity<>(
				response,
				HttpStatus.CREATED);
	}

	@DeleteMapping("/subcategories/{id}")
	public ResponseEntity<ApiResponse<String>> deleteSubCategory(
			@PathVariable Long id) {

		String result =
				service.deleteSubCategory(id);

		ApiResponse<String> response =
				new ApiResponse<>(
						true,
						"Sub category deleted successfully",
						HttpStatus.OK.value(),
						result,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

	// =========================
	// SUPPLIER
	// =========================

	@GetMapping("/suppliers")
	public ResponseEntity<ApiResponse<List<Supplier>>> getSuppliers() {

		List<Supplier> suppliers =
				service.getSuppliers();

		ApiResponse<List<Supplier>> response =
				new ApiResponse<>(
						true,
						"Suppliers fetched successfully",
						HttpStatus.OK.value(),
						suppliers,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/suppliers")
	public ResponseEntity<ApiResponse<Supplier>> addSupplier(
			@RequestBody Supplier s) {

		Supplier saved = service.addSupplier(s);

		ApiResponse<Supplier> response =
				new ApiResponse<>(
						true,
						"Supplier added successfully",
						HttpStatus.CREATED.value(),
						saved,
						LocalDateTime.now());

		return new ResponseEntity<>(
				response,
				HttpStatus.CREATED);
	}

	@PutMapping("/suppliers/{id}")
	public ResponseEntity<ApiResponse<Supplier>> updateSupplier(
			@PathVariable Long id,
			@RequestBody Supplier s) {

		Supplier updated =
				service.updateSupplier(id, s);

		ApiResponse<Supplier> response =
				new ApiResponse<>(
						true,
						"Supplier updated successfully",
						HttpStatus.OK.value(),
						updated,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/suppliers/{id}")
	public ResponseEntity<ApiResponse<String>> deleteSupplier(
			@PathVariable Long id) {

		String result = service.deleteSupplier(id);

		ApiResponse<String> response =
				new ApiResponse<>(
						true,
						"Supplier deleted successfully",
						HttpStatus.OK.value(),
						result,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

	// =========================
	// CUSTOMER
	// =========================

	@GetMapping("/customers")
	public ResponseEntity<ApiResponse<List<Customer>>> getCustomers() {

		List<Customer> customers =
				service.getCustomers();

		ApiResponse<List<Customer>> response =
				new ApiResponse<>(
						true,
						"Customers fetched successfully",
						HttpStatus.OK.value(),
						customers,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/customers")
	public ResponseEntity<ApiResponse<Customer>> addCustomer(
			@RequestBody Customer c) {

		Customer saved = service.addCustomer(c);

		ApiResponse<Customer> response =
				new ApiResponse<>(
						true,
						"Customer added successfully",
						HttpStatus.CREATED.value(),
						saved,
						LocalDateTime.now());

		return new ResponseEntity<>(
				response,
				HttpStatus.CREATED);
	}

	@PutMapping("/customers/{id}")
	public ResponseEntity<ApiResponse<Customer>> updateCustomer(
			@PathVariable Long id,
			@RequestBody Customer c) {

		Customer updated =
				service.updateCustomer(id, c);

		ApiResponse<Customer> response =
				new ApiResponse<>(
						true,
						"Customer updated successfully",
						HttpStatus.OK.value(),
						updated,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/customers/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteCustomer(
			@PathVariable Long id) {

		service.deleteCustomer(id);

		ApiResponse<Void> response =
				new ApiResponse<>(
						true,
						"Customer deleted successfully",
						HttpStatus.OK.value(),
						null,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

	// SHELF LIFE

	@GetMapping("/shelf-lives")
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getShelfLives() {
		List<Map<String, Object>> data = service.getShelfLivesWithMappings();
		ApiResponse<List<Map<String, Object>>> response = new ApiResponse<>(
				true, "Shelf lives fetched successfully",
				HttpStatus.OK.value(), data, LocalDateTime.now());
		return ResponseEntity.ok(response);
	}

	@PostMapping("/shelf-lives")
	public ResponseEntity<ApiResponse<ShelfLife>> addShelfLife(
			@RequestBody Map<String, Object> body) {

		String name = body.get("name").toString();
		Integer months = Integer.valueOf(body.get("months").toString());
		String mappingType = body.get("mappingType").toString();

		List<Long> mappingIds = ((List<?>) body.get("mappingIds"))
				.stream()
				.map(id -> Long.valueOf(id.toString()))
				.collect(java.util.stream.Collectors.toList());

		ShelfLife shelfLife = new ShelfLife();
		shelfLife.setName(name);
		shelfLife.setMonths(months);

		ShelfLife saved = service.addShelfLife(shelfLife, mappingType, mappingIds);

		ApiResponse<ShelfLife> response = new ApiResponse<>(
				true, "Shelf life added successfully",
				HttpStatus.CREATED.value(), saved, LocalDateTime.now());

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@DeleteMapping("/shelf-lives/{id}")
	public ResponseEntity<ApiResponse<String>> deleteShelfLife(
			@PathVariable Long id) {

		String result =
				service.deleteShelfLife(id);

		ApiResponse<String> response =
				new ApiResponse<>(
						true,
						"Shelf life deleted successfully",
						HttpStatus.OK.value(),
						result,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/gst")
	public ResponseEntity<ApiResponse<List<Map<String,Object>>>> getGsts() {

		List<Map<String,Object>> data =
				service.getGsts();

		ApiResponse<List<Map<String,Object>>> response =
				new ApiResponse<>(
						true,
						"GST fetched successfully",
						HttpStatus.OK.value(),
						data,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/gst")
	public ResponseEntity<ApiResponse<GstMaster>> addGst(
			@RequestBody Map<String,Object> body) {

		Integer gstPercentage =
				Integer.valueOf(
						body.get("gstPercentage")
								.toString());

		String mappingType =
				body.get("mappingType")
						.toString();

		List<Long> mappingIds =
				((List<?>) body.get("mappingIds"))
						.stream()
						.map(id -> Long.valueOf(
								id.toString()))
						.toList();

		GstMaster gst =
				new GstMaster();

		gst.setGstPercentage(
				gstPercentage);

		GstMaster saved =
				service.addGst(
						gst,
						mappingType,
						mappingIds);

		ApiResponse<GstMaster> response =
				new ApiResponse<>(
						true,
						"GST added successfully",
						HttpStatus.CREATED.value(),
						saved,
						LocalDateTime.now());

		return new ResponseEntity<>(
				response,
				HttpStatus.CREATED);
	}
	@DeleteMapping("/gst/{id}")
	public ResponseEntity<ApiResponse<String>> deleteGst(
			@PathVariable Long id) {

		String result =
				service.deleteGst(id);

		ApiResponse<String> response =
				new ApiResponse<>(
						true,
						"GST deleted successfully",
						HttpStatus.OK.value(),
						result,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}


}