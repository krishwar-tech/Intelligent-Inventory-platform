package com.inventory.management.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventory.management.dto.ApiResponse;
import com.inventory.management.entity.Settings;
import com.inventory.management.service.SettingsService;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

	private final SettingsService service;

	public SettingsController(SettingsService service) {
		this.service = service;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<Settings>> get() {

		Settings settings = service.getSettings();

		ApiResponse<Settings> response =
				new ApiResponse<>(
						true,
						"Settings fetched successfully",
						HttpStatus.OK.value(),
						settings,
						LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Settings>> save(
			@RequestBody Settings s) {

		Settings saved = service.save(s);

		ApiResponse<Settings> response =
				new ApiResponse<>(
						true,
						"Settings saved successfully",
						HttpStatus.CREATED.value(),
						saved,
						LocalDateTime.now());

		return new ResponseEntity<>(
				response,
				HttpStatus.CREATED);
	}
}