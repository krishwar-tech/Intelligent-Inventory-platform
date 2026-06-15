package com.inventory.management.controller;

import com.inventory.management.dto.ApiResponse;
import com.inventory.management.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Object>> dashboard() {

        Object data = service.dashboard();

        ApiResponse<Object> response =
                new ApiResponse<>(
                        true,
                        "Dashboard report fetched successfully",
                        HttpStatus.OK.value(),
                        data,
                        LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}