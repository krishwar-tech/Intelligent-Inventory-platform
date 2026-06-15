package com.inventory.management.controller;

import com.inventory.management.dto.ApiResponse;
import com.inventory.management.dto.CheckoutRequest;
import com.inventory.management.dto.InvoiceResponse;
import com.inventory.management.service.BillingService;
import org.slf4j.Logger;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService service;

    private static final Logger log =
            LoggerFactory.getLogger(BillingController.class);

    public BillingController(BillingService service) {
        this.service = service;
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<String>> checkout(
           @Valid @RequestBody CheckoutRequest req) {

        log.info("API Checkout request received");

        String billNo = service.checkout(req);

        ApiResponse<String> response =
                new ApiResponse<>(
                        true,
                        "Checkout completed successfully",
                        HttpStatus.CREATED.value(),
                        billNo,
                        LocalDateTime.now());

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED);
    }

    @GetMapping("/invoice/{id}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> invoice(
            @PathVariable Long id) {

        log.info("API Invoice request : {}", id);

        InvoiceResponse invoice = service.getInvoice(id);

        ApiResponse<InvoiceResponse> response =
                new ApiResponse<>(
                        true,
                        "Invoice fetched successfully",
                        HttpStatus.OK.value(),
                        invoice,
                        LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}