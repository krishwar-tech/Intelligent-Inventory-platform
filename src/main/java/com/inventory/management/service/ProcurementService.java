package com.inventory.management.service;

import com.inventory.management.entity.Procurement;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ProcurementService {

	Procurement save(
			Long productId,
			Long supplierId,
			Integer qty,
			Double costPrice,
			LocalDate manufactureDate);

	Map<String,Object> saveGrn(
			Long supplierId,
			String invoiceRef,
			List<Map<String,Object>> items);

	List<Map<String,Object>> getGrnSummary();

	List<Procurement> getGrnDetails(
			String grnNumber);

	void deleteGrn(
			String grnNumber);

	Map<String, Object> importExcel(
			MultipartFile file);

	List<Procurement> getAll();

	void delete(Long id);

	Procurement updatePayment(
			Long id,
			Double amount);
}