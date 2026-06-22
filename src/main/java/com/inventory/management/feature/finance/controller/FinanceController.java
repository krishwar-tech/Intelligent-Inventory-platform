package com.inventory.management.feature.finance.controller;

import java.time.LocalDateTime;

import com.inventory.management.common.dto.ApiResponse;
import com.inventory.management.feature.finance.service.FinanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/finance")
public class FinanceController {

	private final FinanceService service;

	public FinanceController(FinanceService service) {
		this.service = service;
	}

	@GetMapping("/dashboard")
	public ResponseEntity<ApiResponse<Object>> dashboard() {

		Object data = service.dashboard();

		ApiResponse<Object> response =
				new ApiResponse<>(
						true,
						"Finance dashboard fetched successfully",
						HttpStatus.OK.value(),
						data,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}
}