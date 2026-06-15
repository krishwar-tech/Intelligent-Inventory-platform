package com.inventory.management.service.impl;

import com.inventory.management.entity.*;
import com.inventory.management.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.inventory.management.exception.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inventory.management.enums.InventoryActionType;
import com.inventory.management.enums.ProductStatus;

import com.inventory.management.config.TenantContext;
import com.inventory.management.service.InventoryService;
import com.inventory.management.service.ProcurementService;

@Service
public class ProcurementServiceImpl implements ProcurementService {

    private final ProcurementRepository repo;

    private final ProductRepository productRepo;

    private final SupplierRepository supplierRepo;

    private final InventoryService inventoryService;

    private final CategoryRepository categoryRepo;

    private final TenantRepository tenantRepo;

    private final ShelfLifeRepository shelfLifeRepo;

    private static final Logger log =
            LoggerFactory.getLogger(
                    ProcurementServiceImpl.class);

    public ProcurementServiceImpl(
            ProcurementRepository repo,
            TenantRepository tenantRepo,
            ProductRepository productRepo,
            SupplierRepository supplierRepo,
            InventoryService inventoryService,
            CategoryRepository categoryRepo,
            ShelfLifeRepository shelfLifeRepo) {

        this.repo = repo;

        this.productRepo = productRepo;

        this.supplierRepo = supplierRepo;

        this.inventoryService = inventoryService;

        this.categoryRepo = categoryRepo;

        this.tenantRepo = tenantRepo;

        this.shelfLifeRepo = shelfLifeRepo;
    }

    // MANUAL PROCUREMENT

    @Override
    @Transactional
    public Procurement save(
            Long productId,
            Long supplierId,
            Integer qty,
            Double costPrice,
            LocalDate manufactureDate) {

        log.info("Manual procurement started");

        Product product =
                productRepo.findById(productId)
                        .orElseThrow(() -> {

                            log.error(
                                    "Product not found : {}",
                                    productId);

                            return new ProductNotFoundException(
                                    "Product not found");
                        });

        Supplier supplier =
                supplierRepo.findById(supplierId)
                        .orElseThrow(() -> {

                            log.error(
                                    "Supplier not found : {}",
                                    supplierId);

                            return new SupplierNotFoundException(
                                    "Supplier not found");
                        });

        Procurement p =
                new Procurement();

        BigDecimal price =
                BigDecimal.valueOf(costPrice);

        BigDecimal total =
                price.multiply(
                        BigDecimal.valueOf(qty));

        p.setProduct(product);

        p.setSupplier(supplier);

        p.setQty(qty);

        p.setCostPrice(price);

        p.setTotalCost(total);

        p.setPaidAmount(BigDecimal.ZERO);

        p.setDueAmount(total);

        p.setPaymentStatus("UNPAID");

        p.setManufactureDate(manufactureDate);

        ShelfLife shelfLife = null;

        /*
         * Priority 1:
         * Sub Category Shelf Life
         */
        if (product.getSubCategory() != null) {

            shelfLife =
                    shelfLifeRepo
                            .findBySubCategory_Id(
                                    product.getSubCategory().getId())
                            .orElse(null);
        }

        /*
         * Priority 2:
         * Category Shelf Life
         */
        if (
                shelfLife == null &&
                        product.getCategory() != null
        ) {

            shelfLife =
                    shelfLifeRepo
                            .findByCategory_Id(
                                    product.getCategory().getId())
                            .orElse(null);
        }

        /*
         * Apply Expiry
         */
        if (shelfLife != null) {

            p.setShelfLife(shelfLife);

            p.setExpiryDate(
                    manufactureDate.plusMonths(
                            shelfLife.getMonths()
                    )
            );
        }

        p.setDate(LocalDate.now());

        p.setPoNumber(generatePoNumber());

        p.setInvoiceRef(generateInvoiceRef());

        Tenant tenant =
                tenantRepo.findById(
                                TenantContext.getTenantId())
                        .orElseThrow();

        p.setTenant(tenant);

        Procurement saved =
                repo.save(p);

        inventoryService.increaseStock(
                product,
                qty,
                InventoryActionType.PURCHASE,
                "Procurement");

        log.info(
                "Procurement saved successfully | Product : {} | Qty : {}",
                product.getName(),
                qty);

        return saved;
    }

    // EXCEL IMPORT

    @Override
    @Transactional
    public Map<String, Object> importExcel(
            MultipartFile file) {

        log.info("Excel procurement import started");

        int rowsImported = 0;

        int newProducts = 0;

        try {

            InputStream is =
                    file.getInputStream();

            Workbook wb =
                    new XSSFWorkbook(is);

            Sheet sheet =
                    wb.getSheetAt(0);

            DataFormatter formatter =
                    new DataFormatter();

            Row headerRow =
                    sheet.getRow(0);

            Map<String, Integer> columns =
                    new HashMap<>();

            for (Cell cell : headerRow) {

                String header =
                        formatter
                                .formatCellValue(cell)
                                .trim()
                                .toLowerCase();

                if (header.contains("product") ||
                        header.contains("item")) {

                    columns.put(
                            "product",
                            cell.getColumnIndex());
                } else if (
                        header.contains("supplier") ||
                                header.contains("vendor")) {

                    columns.put(
                            "supplier",
                            cell.getColumnIndex());
                } else if (
                        header.contains("qty") ||
                                header.contains("quantity") ||
                                header.contains("stock")) {

                    columns.put(
                            "qty",
                            cell.getColumnIndex());
                } else if (
                        header.contains("category")) {

                    columns.put(
                            "category",
                            cell.getColumnIndex());
                } else if (
                        header.contains("cost/unit") ||
                                header.contains("cost / unit") ||
                                header.contains("unit cost") ||
                                header.equals("cost") ||
                                header.equals("price")) {

                    columns.put(
                            "cost",
                            cell.getColumnIndex());
                } else if (
                        header.contains("barcode") ||
                                header.contains("ean") ||
                                header.contains("upc")) {

                    columns.put(
                            "barcode",
                            cell.getColumnIndex());
                } else if (
                        header.contains("unit") ||
                                header.contains("uom")) {

                    columns.put(
                            "unit",
                            cell.getColumnIndex());
                }
            }

            // ROW PROCESSING

            for (int i = 1;
                 i <= sheet.getLastRowNum();
                 i++) {

                Row row =
                        sheet.getRow(i);

                if (row == null)
                    continue;

                try {

                    String productName =
                            formatter
                                    .formatCellValue(
                                            row.getCell(
                                                    columns.get("product")))
                                    .trim();

                    if (productName.isBlank())
                        continue;

                    log.info(
                            "Processing product : {}",
                            productName);

                    String supplierName =
                            formatter
                                    .formatCellValue(
                                            row.getCell(
                                                    columns.get("supplier")))
                                    .trim();

                    String qtyStr =
                            formatter
                                    .formatCellValue(
                                            row.getCell(
                                                    columns.get("qty")))
                                    .trim();

                    String costStr =
                            formatter
                                    .formatCellValue(
                                            row.getCell(
                                                    columns.get("cost")))
                                    .trim();

                    String categoryName =
                            columns.containsKey("category")
                                    ?
                                    formatter.formatCellValue(
                                                    row.getCell(
                                                            columns.get("category")))
                                            .trim()
                                    :
                                    "";

                    String unit =
                            columns.containsKey("unit")
                                    ?
                                    formatter.formatCellValue(
                                                    row.getCell(
                                                            columns.get("unit")))
                                            .trim()
                                    :
                                    "pcs";

                    // BARCODE

                    String barcode = "";

                    if (columns.containsKey("barcode")) {

                        Cell barcodeCell =
                                row.getCell(
                                        columns.get("barcode"));

                        if (barcodeCell != null) {

                            barcodeCell.setCellType(
                                    CellType.STRING);

                            barcode =
                                    barcodeCell
                                            .getStringCellValue();

                            barcode =
                                    barcode.trim();

                            barcode =
                                    barcode.replace(".0", "");

                            barcode =
                                    barcode.replaceAll(
                                            "\\s+",
                                            "");
                        }
                    }

                    log.info(
                            "Barcode detected : {}",
                            barcode);

                    int qty =
                            qtyStr.isBlank()
                                    ? 0
                                    : Integer.parseInt(qtyStr);

                    double cost =
                            costStr.isBlank()
                                    ? 0
                                    : Double.parseDouble(costStr);

                    Category category = null;

                    if (!categoryName.isBlank()) {

                        Tenant tenant =
                                tenantRepo.findById(
                                                TenantContext.getTenantId())
                                        .orElseThrow();

                        category =
                                categoryRepo
                                        .findByTenant_IdAndNameIgnoreCase(
                                                TenantContext.getTenantId(),
                                                categoryName)
                                        .orElseGet(() -> {

                                            log.info(
                                                    "Creating new category : {}",
                                                    categoryName);

                                            Category c =
                                                    new Category();

                                            c.setName(categoryName);

                                            c.setStatus("ACTIVE");

                                            c.setTenant(tenant);

                                            return categoryRepo.save(c);
                                        });
                    }

                    // PRODUCT CHECK

                    Optional<Product> existing =
                            productRepo
                                    .findByTenant_IdAndNameIgnoreCase(
                                            TenantContext.getTenantId(),
                                            productName);

                    Product product;

                    // EXISTING PRODUCT

                    if (existing.isPresent()) {

                        product =
                                existing.get();

                        log.info(
                                "Existing product found : {}",
                                product.getName());

                        inventoryService.increaseStock(
                                product,
                                qty,
                                InventoryActionType.PURCHASE,
                                "Excel Import");

                        if ((product.getBarcode() == null ||
                                product.getBarcode().isBlank())
                                &&
                                !barcode.isBlank()) {

                            product.setBarcode(barcode);
                        }

                        if (product.getCategory() == null &&
                                category != null) {

                            product.setCategory(category);
                        }

                        productRepo.save(product);

                        log.info(
                                "Stock updated for existing product : {}",
                                product.getName());
                    }

                    // NEW PRODUCT

                    else {

                        product =
                                new Product();

                        Tenant tenant =
                                tenantRepo.findById(
                                                TenantContext.getTenantId())
                                        .orElseThrow();

                        product.setName(productName);

                        product.setBarcode(
                                barcode.isBlank()
                                        ? null
                                        : barcode);

                        product.setCategory(category);

                        product.setUnit(
                                unit.isBlank()
                                        ? "pcs"
                                        : unit);

                        product.setStock(qty);

                        product.setSku(null);

                        product.setStatus(
                                ProductStatus.PENDING);

                        product.setTenant(tenant);

                        product =
                                productRepo.save(product);

                        newProducts++;

                        log.info(
                                "New product created : {}",
                                productName);
                    }

                    // SUPPLIER

                    Tenant tenant =
                            tenantRepo.findById(
                                            TenantContext.getTenantId())
                                    .orElseThrow();

                    Supplier supplier =
                            supplierRepo
                                    .findByTenant_IdAndNameIgnoreCase(
                                            TenantContext.getTenantId(),
                                            supplierName)
                                    .orElseGet(() -> {

                                        log.info(
                                                "Creating supplier : {}",
                                                supplierName);

                                        Supplier s =
                                                new Supplier();

                                        s.setName(supplierName);

                                        s.setStatus("ACTIVE");

                                        s.setTenant(tenant);

                                        return supplierRepo.save(s);
                                    });

                    // PROCUREMENT ENTRY

                    Procurement p =
                            new Procurement();

                    BigDecimal price =
                            BigDecimal.valueOf(cost);

                    BigDecimal total =
                            price.multiply(
                                    BigDecimal.valueOf(qty));

                    p.setProduct(product);

                    p.setSupplier(supplier);

                    p.setQty(qty);

                    p.setCostPrice(price);

                    p.setTotalCost(total);

                    p.setPaidAmount(BigDecimal.ZERO);

                    p.setDueAmount(total);

                    p.setPaymentStatus("UNPAID");

                    p.setDate(LocalDate.now());

                    p.setPoNumber(generatePoNumber());

                    p.setInvoiceRef(generateInvoiceRef());

                    p.setTenant(tenant);

                    repo.save(p);

                    log.info(
                            "Procurement saved for : {}",
                            product.getName());

                    rowsImported++;

                } catch (Exception e) {

                    log.error(
                            "Row {} import failed : {}",
                            i,
                            e.getMessage());
                }
            }

            wb.close();

            log.info(
                    "Excel import completed successfully");
        } catch (Exception e) {

            log.error(
                    "Excel import failed : {}",
                    e.getMessage());

            throw new RuntimeException(
                    "Import failed : " + e.getMessage());
        }

        Map<String, Object> res =
                new HashMap<>();

        res.put("rowsImported", rowsImported);

        res.put("newProducts", newProducts);

        log.info(
                "Rows Imported : {} | New Products : {}",
                rowsImported,
                newProducts);

        return res;
    }

    @Override
    public List<Procurement> getAll() {

        log.info(
                "Fetching procurement history");

        List<Procurement> list =
                repo.findByTenant_Id(
                        TenantContext.getTenantId());

        log.info(
                "Total procurement records : {}",
                list.size());

        return list;
    }

    // PROCUREMENT DELETE

    @Override
    @Transactional
    public void delete(Long id) {

        log.info(
                "Deleting procurement ID : {}",
                id);

        Procurement p =
                repo.findById(id)
                        .orElseThrow(() -> {

                            log.error(
                                    "Procurement not found : {}",
                                    id);

                            return new ProcurementNotFoundException(
                                    "Procurement not found");
                        });

        repo.delete(p);

        log.info(
                "Procurement deleted successfully");
    }

    private String generatePoNumber() {

        Long tenantId = TenantContext.getTenantId();

        Optional<Procurement> latest =
                repo.findTopByTenant_IdOrderByIdDesc(
                        tenantId);

        int next = 1;

        if (latest.isPresent()) {

            String lastPo =
                    latest.get().getPoNumber();

            if (lastPo != null &&
                    lastPo.contains("-")) {

                String[] arr =
                        lastPo.split("-");

                next =
                        Integer.parseInt(arr[2]) + 1;
            }
        }

        LocalDate now = LocalDate.now();

        String yearMonth =
                now.getYear() +
                        String.format("%02d",
                                now.getMonthValue());

        return "PO-" +
                yearMonth +
                "-" +
                String.format("%04d", next);
    }

    private String generateInvoiceRef() {

        Long tenantId = TenantContext.getTenantId();

        Optional<Procurement> latest =
                repo.findTopByTenant_IdOrderByIdDesc(
                        tenantId);

        int next = 1;

        if (latest.isPresent()) {

            String lastRef =
                    latest.get().getInvoiceRef();

            if (lastRef != null &&
                    lastRef.contains("-")) {

                String[] arr =
                        lastRef.split("-");

                next =
                        Integer.parseInt(arr[2]) + 1;
            }
        }

        LocalDate now = LocalDate.now();

        String yearMonth =
                now.getYear() +
                        String.format("%02d",
                                now.getMonthValue());

        return "PRC-" +
                yearMonth +
                "-" +
                String.format("%04d", next);
    }

    // PAYMENT UPDATE

    @Override
    @Transactional
    public Procurement updatePayment(
            Long id,
            Double amount) {

        log.info(
                "Updating procurement payment | ID : {} | Amount : {}",
                id,
                amount);

        Procurement p =
                repo.findById(id)
                        .orElseThrow(() -> {

                            log.error(
                                    "Procurement not found : {}",
                                    id);

                            return new ProcurementNotFoundException(
                                    "Procurement not found");
                        });

        BigDecimal payAmount =
                BigDecimal.valueOf(amount);

        BigDecimal currentPaid =
                p.getPaidAmount() == null
                        ? BigDecimal.ZERO
                        : p.getPaidAmount();

        BigDecimal total =
                p.getTotalCost() == null
                        ? BigDecimal.ZERO
                        : p.getTotalCost();

        BigDecimal newPaid =
                currentPaid.add(payAmount);

        // PREVENT OVERPAYMENT

        if (newPaid.compareTo(total) > 0) {

            log.warn(
                    "Overpayment prevented for procurement ID : {}",
                    id);

            newPaid = total;
        }

        BigDecimal due =
                total.subtract(newPaid);

        p.setPaidAmount(newPaid);

        p.setDueAmount(due);

        // STATUS LOGIC

        if (due.compareTo(BigDecimal.ZERO) == 0) {

            p.setPaymentStatus("PAID");

            log.info(
                    "Procurement fully paid : {}",
                    id);
        } else if (
                newPaid.compareTo(BigDecimal.ZERO) > 0) {

            p.setPaymentStatus("PARTIAL");

            log.info(
                    "Procurement partially paid : {}",
                    id);
        } else {

            p.setPaymentStatus("UNPAID");
        }

        Procurement saved =
                repo.save(p);

        log.info(
                "Payment updated successfully for procurement ID : {}",
                id);

        return saved;
    }
}