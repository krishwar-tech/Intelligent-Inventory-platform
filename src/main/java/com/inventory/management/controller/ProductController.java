package com.inventory.management.controller;

import com.inventory.management.config.TenantContext;
import com.inventory.management.dto.ApiResponse;
import com.inventory.management.entity.Category;
import com.inventory.management.entity.Product;
import com.inventory.management.enums.ProductStatus;
import com.inventory.management.repository.CategoryRepository;
import com.inventory.management.repository.ProductRepository;
import com.inventory.management.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inventory.management.entity.SubCategory;
import com.inventory.management.repository.SubCategoryRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepo;

    private final ProductService service;

    private final CategoryRepository categoryRepo;

    private final SubCategoryRepository subCategoryRepo;

    private static final Logger log =
            LoggerFactory.getLogger(ProductController.class);

    public ProductController(
            ProductService service,
            CategoryRepository categoryRepo,
            ProductRepository productRepo,
            SubCategoryRepository subCategoryRepo) {

        this.service = service;
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
        this.subCategoryRepo = subCategoryRepo;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> create(
            @RequestBody Map<String, Object> body) {

        log.info("API Create Product called");

        Product p = new Product();

        p.setName(
                body.get("name") != null
                        ? body.get("name").toString()
                        : "");

        String barcode =
                body.get("barcode") != null
                        ? body.get("barcode").toString().trim()
                        : "";

        p.setBarcode(
                barcode.isEmpty()
                        ? "N/A"
                        : barcode);

        p.setPrice(
                body.get("price") != null
                        ? new BigDecimal(body.get("price").toString())
                        : BigDecimal.ZERO);

        p.setMrp(
                body.get("mrp") != null
                        ? new BigDecimal(body.get("mrp").toString())
                        : BigDecimal.ZERO);

        p.setUnit(
                body.get("unit") != null
                        ? body.get("unit").toString()
                        : "pcs");

        p.setPackSize(
                body.get("packSize") != null
                        ? Integer.valueOf(
                        body.get("packSize").toString())
                        : 0);

        p.setStatus(ProductStatus.PENDING);

        if (body.get("categoryId") != null) {

            Long catId =
                    Long.valueOf(
                            body.get("categoryId").toString());

            Category c =
                    categoryRepo.findById(catId).orElseThrow();

            p.setCategory(c);
        }

        if (body.get("subCategoryId") != null) {

            Long subId =
                    Long.valueOf(
                            body.get("subCategoryId").toString());

            SubCategory sc =
                    subCategoryRepo.findById(subId)
                            .orElseThrow();

            p.setSubCategory(sc);

            // AUTO ASSIGN PARENT CATEGORY

            p.setCategory(sc.getCategory());
        }

        if (
                body.get("categoryId") == null &&
                        body.get("subCategoryId") == null
        ) {
            throw new RuntimeException(
                    "Either Category or Sub Category is required");
        }

        Product saved = service.save(p);

        log.info("API Product created : {}", saved.getName());

        ApiResponse<Product> response =
                new ApiResponse<>(
                        true,
                        "Product created successfully",
                        HttpStatus.CREATED.value(),
                        saved,
                        LocalDateTime.now());

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAll() {

        log.info("API Fetch All Products");

        List<Product> products = service.getAll();

        ApiResponse<List<Product>> response =
                new ApiResponse<>(
                        true,
                        "Products fetched successfully",
                        HttpStatus.OK.value(),
                        products,
                        LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getById(
            @PathVariable Long id) {

        log.info("API Fetch Product by ID : {}", id);

        Product product = service.getById(id);

        ApiResponse<Product> response =
                new ApiResponse<>(
                        true,
                        "Product fetched successfully",
                        HttpStatus.OK.value(),
                        product,
                        LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Product>>> search(
            @RequestParam String q) {

        log.info("API Product Search : {}", q);

        List<Product> products = service.search(q);

        ApiResponse<List<Product>> response =
                new ApiResponse<>(
                        true,
                        "Search completed successfully",
                        HttpStatus.OK.value(),
                        products,
                        LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<ApiResponse<List<Product>>> byCategory(
            @PathVariable Long id) {

        log.info("API Products By Category : {}", id);

        List<Product> products =
                service.getByCategory(id);

        ApiResponse<List<Product>> response =
                new ApiResponse<>(
                        true,
                        "Category products fetched successfully",
                        HttpStatus.OK.value(),
                        products,
                        LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> update(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        log.info("API Update Product : {}", id);

        Product p = new Product();

        if (body.get("name") != null)
            p.setName(body.get("name").toString());

        if (body.get("barcode") != null)
            p.setBarcode(body.get("barcode").toString());

        if (body.get("price") != null)
            p.setPrice(BigDecimal.valueOf(Double.parseDouble(body.get("price").toString())));

        if (body.get("mrp") != null)
            p.setMrp(BigDecimal.valueOf(Double.parseDouble(body.get("mrp").toString())));

        if (body.get("unit") != null)
            p.setUnit(body.get("unit").toString());

        // ✅ ADD THIS
        if (body.get("packSize") != null)
            p.setPackSize(Integer.valueOf(body.get("packSize").toString()));

        Long categoryId = body.get("categoryId") == null
                ? null : Long.valueOf(body.get("categoryId").toString());

        Long subCategoryId = body.get("subCategoryId") == null
                ? null : Long.valueOf(body.get("subCategoryId").toString());

        Product updated = service.update(id, p, categoryId, subCategoryId);

        ApiResponse<Product> response = new ApiResponse<>(
                true,
                "Product updated successfully",
                HttpStatus.OK.value(),
                updated,
                LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/price/{id}")
    public ResponseEntity<ApiResponse<Product>> updatePrice(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        log.info("API Update Price : {}", id);

        Double price =
                Double.valueOf(body.get("price").toString());

        Double mrp =
                Double.valueOf(body.get("mrp").toString());

        Product updated =
                service.updatePrice(id, price, mrp);

        ApiResponse<Product> response =
                new ApiResponse<>(
                        true,
                        "Product price updated successfully",
                        HttpStatus.OK.value(),
                        updated,
                        LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<ApiResponse<Product>> activate(
            @PathVariable Long id) {

        log.info("API Activate Product : {}", id);

        Product product = service.activate(id);

        ApiResponse<Product> response =
                new ApiResponse<>(
                        true,
                        "Product activated successfully",
                        HttpStatus.OK.value(),
                        product,
                        LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<ApiResponse<Product>> deactivate(
            @PathVariable Long id) {

        log.warn("API Deactivate Product : {}", id);

        Product product = service.deactivate(id);

        ApiResponse<Product> response =
                new ApiResponse<>(
                        true,
                        "Product deactivated successfully",
                        HttpStatus.OK.value(),
                        product,
                        LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<ApiResponse<Product>> approve(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        log.info("API Approve Product : {}", id);

        Double price =
                Double.valueOf(body.get("price").toString());

        Double mrp =
                Double.valueOf(body.get("mrp").toString());

        Product product =
                service.approve(id, price, mrp);

        ApiResponse<Product> response =
                new ApiResponse<>(
                        true,
                        "Product approved successfully",
                        HttpStatus.OK.value(),
                        product,
                        LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(
            @PathVariable Long id) {

        log.warn("API Delete Product : {}", id);

        service.delete(id);

        ApiResponse<String> response =
                new ApiResponse<>(
                        true,
                        "Product deleted successfully",
                        HttpStatus.OK.value(),
                        "Deleted",
                        LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<Product>>> pending() {

        log.info("API Fetch Pending Products");

        List<Product> products = service.getPending();

        ApiResponse<List<Product>> response =
                new ApiResponse<>(
                        true,
                        "Pending products fetched successfully",
                        HttpStatus.OK.value(),
                        products,
                        LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> stats() {

        log.info("API Product Stats");

        Map<String, Object> stats =
                service.stats();

        ApiResponse<Map<String, Object>> response =
                new ApiResponse<>(
                        true,
                        "Product stats fetched successfully",
                        HttpStatus.OK.value(),
                        stats,
                        LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/scan/{barcode}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> scan(
            @PathVariable String barcode) {

        log.info("API Barcode Scan : {}", barcode);

        Map<String, Object> res = new HashMap<>();

        Optional<Product> product =
                productRepo.findByTenant_IdAndBarcode(
                        TenantContext.getTenantId(),
                        barcode.trim());

        if (product.isPresent()) {

            log.info(
                    "Barcode matched : {}",
                    product.get().getName());

            res.put("exists", true);
            res.put("product", product.get());

        } else {

            log.warn("Barcode not found : {}", barcode);

            res.put("exists", false);
        }

        ApiResponse<Map<String, Object>> response =
                new ApiResponse<>(
                        true,
                        "Barcode scan completed",
                        HttpStatus.OK.value(),
                        res,
                        LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}