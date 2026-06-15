package com.inventory.management.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.inventory.management.dto.ApiResponse;
import com.inventory.management.entity.Procurement;
import com.inventory.management.service.ProcurementService;

@RestController
@RequestMapping("/api/procurement")
public class ProcurementController {

    private final ProcurementService service;

    private static final Logger log =
            LoggerFactory.getLogger(ProcurementController.class);

    public ProcurementController(ProcurementService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Procurement>>> getAll() {

        log.info("API Fetch Procurement History");

        List<Procurement> data = service.getAll();

        ApiResponse<List<Procurement>> response =
                new ApiResponse<>(
                        true,
                        "Procurement history fetched successfully",
                        HttpStatus.OK.value(),
                        data,
                        LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/import-excel")
    public ResponseEntity<ApiResponse<Map<String, Object>>> importExcel(
            @RequestParam("file") MultipartFile file) {

        log.info("API Excel Import Started");

        Map<String, Object> data = service.importExcel(file);

        ApiResponse<Map<String, Object>> response =
                new ApiResponse<>(
                        true,
                        "Excel imported successfully",
                        HttpStatus.OK.value(),
                        data,
                        LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(
            @PathVariable Long id) {

        log.warn("API Delete Procurement : {}", id);

        service.delete(id);

        ApiResponse<String> response =
                new ApiResponse<>(
                        true,
                        "Procurement deleted successfully",
                        HttpStatus.OK.value(),
                        "Deleted",
                        LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/payment/{id}")
    public ResponseEntity<ApiResponse<Procurement>> updatePayment(
            @PathVariable Long id,
            @RequestParam Double amount) {

        log.info(
                "API Procurement Payment Update | ID : {} | Amount : {}",
                id,
                amount);

        Procurement procurement =
                service.updatePayment(id, amount);

        ApiResponse<Procurement> response =
                new ApiResponse<>(
                        true,
                        "Payment updated successfully",
                        HttpStatus.OK.value(),
                        procurement,
                        LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Procurement>> save(
            @RequestBody Map<String, Object> body) {

        Long productId =
                Long.valueOf(body.get("productId").toString());

        Long supplierId =
                Long.valueOf(body.get("supplierId").toString());

        Integer qty =
                Integer.valueOf(body.get("qty").toString());

        Double costPrice =
                Double.valueOf(body.get("costPrice").toString());

        if (body.get("manufactureDate") == null) {
            throw new RuntimeException("Manufacture Date is required");
        }

        LocalDate manufactureDate =
                LocalDate.parse(
                        body.get("manufactureDate").toString());

        Procurement procurement =
                service.save(
                        productId,
                        supplierId,
                        qty,
                        costPrice,
                        manufactureDate);

        ApiResponse<Procurement> response =
                new ApiResponse<>(
                        true,
                        "Procurement created successfully",
                        HttpStatus.CREATED.value(),
                        procurement,
                        LocalDateTime.now());

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED);
    }
}