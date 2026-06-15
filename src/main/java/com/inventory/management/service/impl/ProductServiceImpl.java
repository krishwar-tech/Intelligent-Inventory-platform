package com.inventory.management.service.impl;

import com.inventory.management.entity.*;
import com.inventory.management.repository.*;
import com.inventory.management.enums.ProductStatus;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.exception.*;
import com.inventory.management.service.ProductService;

import java.math.BigDecimal;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private SaleItemRepository saleItemRepo;

    private final ProductRepository productRepo;

    private final SubCategoryRepository subCategoryRepo;

    private final CategoryRepository categoryRepo;

    private final ProcurementRepository procurementRepo;

    private final InventoryRepository inventoryRepo;

    private final TenantRepository tenantRepo;

    private static final Logger log =
            LoggerFactory.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(
            ProductRepository productRepo,
            CategoryRepository categoryRepo,
            ProcurementRepository procurementRepo,
            SubCategoryRepository subCategoryRepo,
            InventoryRepository inventoryRepo,
            TenantRepository tenantRepo) {

        this.productRepo = productRepo;

        this.categoryRepo = categoryRepo;

        this.procurementRepo = procurementRepo;

        this.subCategoryRepo = subCategoryRepo;

        this.inventoryRepo = inventoryRepo;

        this.tenantRepo = tenantRepo;
    }

    @Override
    public Product save(Product p) {

        log.info("Creating product : {}", p.getName());

        validateBarcode(p.getBarcode(), null);

        generateSku(p);

        applyDefaults(p);

        Tenant tenant =
                tenantRepo.findById(
                                TenantContext.getTenantId())
                        .orElseThrow();

        p.setTenant(tenant);

        Product saved = productRepo.save(p);

        log.info(
                "Product created successfully : {} | SKU : {}",
                saved.getName(),
                saved.getSku());

        return saved;
    }

    @Override
    public List<Product> getAll() {

        log.info("Fetching all products");

        List<Product> products =
                productRepo.findByTenant_Id(
                        TenantContext.getTenantId());

        log.info("Total products fetched : {}", products.size());

        return products;
    }

    @Override
    public Product getById(Long id) {

        log.info("Fetching product by ID : {}", id);

        return productRepo
                .findByIdAndTenant_Id(
                        id,
                        TenantContext.getTenantId())
                .orElseThrow(() -> {

                    log.error(
                            "Product not found for tenant : {}",
                            id);

                    return new ProductNotFoundException(
                            "Product not found");
                });
    }

    @Override
    public Product getByBarcode(String code) {

        log.info("Searching product by barcode : {}", code);

        Product product =
                productRepo.findByTenant_IdAndBarcode(
                                TenantContext.getTenantId(),
                                code)
                        .orElse(null);

        if (product == null) {

            log.warn("No product found for barcode : {}", code);

        } else {

            log.info(
                    "Barcode matched product : {}",
                    product.getName());
        }

        return product;
    }

    @Override
    public List<Product> getActive() {

        log.info("Fetching active products");

        return productRepo.findByTenant_IdAndStatus(
                TenantContext.getTenantId(),
                ProductStatus.ACTIVE);
    }

    @Override
    public List<Product> getPending() {

        log.info("Fetching pending products");

        return productRepo.findByTenant_IdAndStatus(
                TenantContext.getTenantId(),
                ProductStatus.PENDING);
    }

    @Override
    public Map<String, Object> stats() {

        log.info("Generating product statistics");

        List<Product> all =
                productRepo.findByTenant_Id(
                        TenantContext.getTenantId());

        long active = all.stream()
                .filter(p ->
                        p.getStatus() != null &&
                                p.getStatus().name().equals(ProductStatus.ACTIVE.name()))
                .count();

        long pending = all.stream()
                .filter(p ->
                        p.getStatus() != null &&
                                p.getStatus().name().equals(ProductStatus.PENDING.name()))
                .count();

        long low = all.stream()
                .filter(p ->
                        (p.getStock() != null
                                ? p.getStock()
                                : 0)
                                <=
                                (p.getReorderLevel() != null
                                        ? p.getReorderLevel()
                                        : 10))
                .count();

        Map<String, Object> map = new HashMap<>();

        map.put("total", all.size());

        map.put("active", active);

        map.put("pending", pending);

        map.put("lowStock", low);

        log.info("Stats generated successfully");

        return map;
    }

    @Override
    public List<Product> getByCategory(Long categoryId) {

        log.info(
                "Fetching products by category : {}",
                categoryId);

        return productRepo.findByTenant_IdAndCategory_Id(
                TenantContext.getTenantId(),
                categoryId);
    }

    @Override
    public List<Product> search(String keyword) {

        log.info(
                "Product search keyword : {}",
                keyword);

        if (keyword == null || keyword.isBlank()) {

            return productRepo.findByTenant_Id(
                    TenantContext.getTenantId());
        }

        return productRepo
                .findByTenant_IdAndNameContainingIgnoreCase(
                        TenantContext.getTenantId(),
                        keyword);
    }

    @Override
    public Product update(
            Long id,
            Product body,
            Long categoryId,
            Long subCategoryId) {

        log.info("Updating product ID : {}", id);

        Product p = getById(id);

        if (body.getBarcode() != null &&
                !body.getBarcode().isBlank()) {

            validateBarcode(body.getBarcode(), id);

            p.setBarcode(body.getBarcode());
        }

        if (body.getName() != null)
            p.setName(body.getName());

        if (body.getUnit() != null)
            p.setUnit(body.getUnit());

        if (body.getPrice() != null)
            p.setPrice(body.getPrice());

        if (body.getMrp() != null)
            p.setMrp(body.getMrp());

        if (categoryId != null) {

            Category category =
                    categoryRepo.findById(categoryId)
                            .orElseThrow();

            p.setCategory(category);
        }

        if (subCategoryId != null) {

            SubCategory sub =
                    subCategoryRepo.findById(subCategoryId)
                            .orElseThrow();

            p.setSubCategory(sub);
        }

        Product updated = productRepo.save(p);

        log.info(
                "Product updated successfully : {}",
                updated.getName());

        return updated;
    }

    @Override
    public Product updatePrice(
            Long id,
            Double price,
            Double mrp) {

        log.info(
                "Updating price for product ID : {}",
                id);

        Product p = getById(id);

        p.setPrice(BigDecimal.valueOf(price));

        p.setMrp(BigDecimal.valueOf(mrp));

        Product updated = productRepo.save(p);

        log.info(
                "Price updated for : {}",
                updated.getName());

        return updated;
    }

    @Override
    public Product approve(
            Long id,
            Double price,
            Double mrp) {

        log.info("Approving product ID : {}", id);

        Product p = getById(id);

        if (p.getSku() == null ||
                p.getSku().isBlank()) {

            generateSku(p);

            log.info(
                    "SKU generated : {}",
                    p.getSku());
        }

        p.setPrice(BigDecimal.valueOf(price));

        p.setMrp(BigDecimal.valueOf(mrp));

        p.setStatus(ProductStatus.ACTIVE);

        Product approved = productRepo.save(p);

        log.info(
                "Product approved successfully : {}",
                approved.getName());

        return approved;
    }

    @Override
    @Transactional
    public Product delete(Long id) {

        log.warn("Deleting product ID : {}", id);

        Product product = getById(id);

        inventoryRepo.deleteByProduct(product);

        procurementRepo.deleteByProduct_Id(id);

        saleItemRepo.deleteByProduct(product);

        productRepo.delete(product);

        log.warn(
                "Product deleted successfully : {}",
                product.getName());

        return product;
    }

    private void validateBarcode(
            String barcode,
            Long currentId) {

        if (barcode == null
                || barcode.isBlank()
                || "N/A".equalsIgnoreCase(barcode.trim())) {
            return;
        }

        Optional<Product> exists =
                productRepo.findByTenant_IdAndBarcode(
                        TenantContext.getTenantId(),
                        barcode.trim());

        if (exists.isPresent()) {

            if (currentId == null ||
                    !exists.get().getId().equals(currentId)) {

                log.error(
                        "Duplicate barcode detected : {}",
                        barcode);

                throw new DuplicateResourceException(
                        "Barcode already exists");
            }
        }
    }

    private void generateSku(Product p) {

        if (p.getSku() != null &&
                !p.getSku().isBlank())
            return;

        String prefix =
                p.getName() == null
                        ? "PRD"
                        : p.getName()
                        .substring(
                                0,
                                Math.min(
                                        3,
                                        p.getName().length()))
                        .toUpperCase();

        String random =
                String.valueOf(
                        (int) (Math.random() * 9000) + 1000);

        p.setSku(prefix + "-" + random);

        log.info("Generated SKU : {}", p.getSku());
    }

    private void applyDefaults(Product p) {

        if (p.getStock() == null)
            p.setStock(0);

        if (p.getReorderLevel() == null)
            p.setReorderLevel(10);

        if (p.getSafetyStock() == null)
            p.setSafetyStock(5);

        if (p.getPrice() == null)
            p.setPrice(BigDecimal.ZERO);

        if (p.getMrp() == null)
            p.setMrp(BigDecimal.ZERO);

        if (p.getUnit() == null)
            p.setUnit("pcs");

        if (p.getStatus() == null)
            p.setStatus(ProductStatus.ACTIVE);
    }

    @Override
    public Product activate(Long id) {

        log.info("Activating product ID : {}", id);

        Product p = getById(id);

        p.setStatus(ProductStatus.ACTIVE);

        Product updated = productRepo.save(p);

        log.info(
                "Product activated : {}",
                updated.getName());

        return updated;
    }

    @Override
    public Product deactivate(Long id) {

        log.warn("Deactivating product ID : {}", id);

        Product p = getById(id);

        p.setStatus(ProductStatus.INACTIVE);

        Product updated = productRepo.save(p);

        log.warn(
                "Product deactivated : {}",
                updated.getName());

        return updated;
    }
}