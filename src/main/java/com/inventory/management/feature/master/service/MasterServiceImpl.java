package com.inventory.management.feature.master.service;


import com.inventory.management.feature.auth.entity.Tenant;
import com.inventory.management.feature.auth.repository.TenantRepository;
import com.inventory.management.feature.billing.entity.Customer;
import com.inventory.management.feature.billing.entity.SaleItem;
import com.inventory.management.feature.billing.repository.CustomerRepository;
import com.inventory.management.feature.billing.repository.SaleItemRepository;
import com.inventory.management.feature.master.entity.*;
import com.inventory.management.feature.master.repository.*;
import com.inventory.management.feature.product.entity.Product;
import org.springframework.stereotype.Service;
import java.util.*;


import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inventory.management.config.TenantContext;

@Service
public class MasterServiceImpl implements MasterService {

    private final CategoryRepository categoryRepo;

    private final GstRepository gstRepo;

    private final SubCategoryRepository subCategoryRepo;

    private final SupplierRepository supplierRepo;

    private final CustomerRepository customerRepo;

    private final SaleItemRepository itemRepo;

    private final TenantRepository tenantRepo;

    private final ShelfLifeRepository shelfLifeRepo;

    private static final Logger log =
            LoggerFactory.getLogger(MasterServiceImpl.class);

    public MasterServiceImpl(
            CategoryRepository categoryRepo,
            SupplierRepository supplierRepo,
            CustomerRepository customerRepo,
            SaleItemRepository itemRepo,
            ShelfLifeRepository shelfLifeRepo,
            GstRepository gstRepo,
            TenantRepository tenantRepo,
            SubCategoryRepository subCategoryRepo) {

        this.categoryRepo = categoryRepo;

        this.gstRepo = gstRepo;

        this.supplierRepo = supplierRepo;

        this.shelfLifeRepo = shelfLifeRepo;

        this.customerRepo = customerRepo;

        this.itemRepo = itemRepo;

        this.subCategoryRepo = subCategoryRepo;

        this.tenantRepo = tenantRepo;
    }

    @Override
    public GstMaster addGst(
            GstMaster gst,
            String mappingType,
            List<Long> mappingIds) {

        Tenant tenant =
                tenantRepo.findById(
                                TenantContext.getTenantId())
                        .orElseThrow();

        GstMaster lastSaved = null;

        if ("subcategory".equals(mappingType)) {

            for (Long subCategoryId : mappingIds) {

                SubCategory subCategory =
                        subCategoryRepo.findById(subCategoryId)
                                .orElseThrow();

                GstMaster newGst =
                        new GstMaster();

                newGst.setGstPercentage(
                        gst.getGstPercentage());

                newGst.setStatus("ACTIVE");

                newGst.setTenant(tenant);

                newGst.setSubCategory(subCategory);

                newGst.setMappingNames(
                        subCategory.getName());

                lastSaved =
                        gstRepo.save(newGst);
            }
        } else if ("category".equals(mappingType)) {

            for (Long categoryId : mappingIds) {

                Category category =
                        categoryRepo.findById(categoryId)
                                .orElseThrow();

                GstMaster newGst =
                        new GstMaster();

                newGst.setGstPercentage(
                        gst.getGstPercentage());

                newGst.setStatus("ACTIVE");

                newGst.setTenant(tenant);

                newGst.setCategory(category);

                newGst.setMappingNames(
                        category.getName());

                lastSaved =
                        gstRepo.save(newGst);
            }
        }

        return lastSaved;
    }

    @Override
    public List<Map<String, Object>> getGsts() {

        List<GstMaster> gsts =
                gstRepo.findByTenant_IdAndStatus(
                        TenantContext.getTenantId(),
                        "ACTIVE");

        List<Map<String, Object>> result =
                new ArrayList<>();

        for (GstMaster gst : gsts) {

            Map<String, Object> map =
                    new HashMap<>();

            map.put("id", gst.getId());

            map.put("gstPercentage",
                    gst.getGstPercentage());

            map.put("mappingName",
                    gst.getMappingNames());

            result.add(map);
        }

        return result;
    }

    @Override
    public String deleteGst(Long id) {

        GstMaster gst =
                gstRepo.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "GST not found"));

        gstRepo.delete(gst);

        return "Deleted successfully";
    }

    @Override
    public ShelfLife addShelfLife(
            ShelfLife shelfLife,
            String mappingType,
            List<Long> mappingIds) {

        Tenant tenant =
                tenantRepo.findById(
                                TenantContext.getTenantId())
                        .orElseThrow();

        ShelfLife lastSaved = null;

        if ("subcategory".equals(mappingType)) {

            for (Long subCategoryId : mappingIds) {

                SubCategory subCategory =
                        subCategoryRepo.findById(subCategoryId)
                                .orElseThrow();

                ShelfLife newShelfLife =
                        new ShelfLife();

                newShelfLife.setName(
                        shelfLife.getName());

                newShelfLife.setMonths(
                        shelfLife.getMonths());

                newShelfLife.setStatus("ACTIVE");

                newShelfLife.setTenant(tenant);

                newShelfLife.setSubCategory(subCategory);

                newShelfLife.setMappingNames(
                        subCategory.getName());

                lastSaved =
                        shelfLifeRepo.save(newShelfLife);
            }
        } else if ("category".equals(mappingType)) {

            for (Long categoryId : mappingIds) {

                Category category =
                        categoryRepo.findById(categoryId)
                                .orElseThrow();

                ShelfLife newShelfLife =
                        new ShelfLife();

                newShelfLife.setName(
                        shelfLife.getName());

                newShelfLife.setMonths(
                        shelfLife.getMonths());

                newShelfLife.setStatus("ACTIVE");

                newShelfLife.setTenant(tenant);

                newShelfLife.setCategory(category);

                newShelfLife.setMappingNames(
                        category.getName());

                lastSaved =
                        shelfLifeRepo.save(newShelfLife);
            }
        }

        return lastSaved;
    }

    @Override
    public List<Map<String, Object>> getShelfLivesWithMappings() {
        List<ShelfLife> lives = shelfLifeRepo.findByTenant_IdAndStatus(
                TenantContext.getTenantId(), "ACTIVE");

        List<Map<String, Object>> result = new ArrayList<>();
        for (ShelfLife sl : lives) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sl.getId());
            map.put("name", sl.getName());
            map.put("months", sl.getMonths());
            map.put("mappingName", sl.getMappingNames() != null ? sl.getMappingNames() :
                    sl.getCategory() != null ? sl.getCategory().getName() :
                    sl.getSubCategory() != null ? sl.getSubCategory().getName() : "—");

            result.add(map);
        }
        return result;
    }

    // CATEGORY

    @Override
    public List<Map<String, Object>> getCategories() {

        log.info("Fetching active categories");

        List<Category> categories =
                categoryRepo.findByTenant_IdAndStatus(
                        TenantContext.getTenantId(),
                        "ACTIVE");

        List<SaleItem> soldItems =
                itemRepo.findAll();

        Set<Long> billedCategoryIds =
                new HashSet<>();

        for (SaleItem item : soldItems) {

            Product product =
                    item.getProduct();

            if (product != null &&
                    product.getCategory() != null) {

                billedCategoryIds.add(
                        product.getCategory().getId());
            }
        }

        List<Map<String, Object>> result =
                new ArrayList<>();

        for (Category c : categories) {

            boolean billed =
                    billedCategoryIds.contains(
                            c.getId());

            Map<String, Object> map =
                    new HashMap<>();

            map.put("id", c.getId());

            map.put("name", c.getName());

            map.put("status", c.getStatus());

            map.put("canDelete", !billed);

            result.add(map);
        }

        log.info(
                "Total active categories fetched : {}",
                result.size());

        return result;
    }

    @Override
    public Category addCategory(Category c) {

        log.info(
                "Creating category : {}",
                c.getName());

        Tenant tenant =
                tenantRepo.findById(
                                TenantContext.getTenantId())
                        .orElseThrow();

        c.setTenant(tenant);

        c.setStatus("ACTIVE");

        Category saved =
                categoryRepo.save(c);

        log.info(
                "Category created successfully : {}",
                saved.getName());

        return saved;
    }

    @Override
    public Category updateCategory(
            Long id,
            Category body) {

        log.info(
                "Updating category ID : {}",
                id);

        Category c =
                categoryRepo.findById(id)
                        .orElseThrow(() -> {

                            log.error(
                                    "Category not found : {}",
                                    id);

                            return new RuntimeException(
                                    "Category not found");
                        });

        c.setName(body.getName());

        Category updated =
                categoryRepo.save(c);

        log.info(
                "Category updated successfully : {}",
                updated.getName());

        return updated;
    }

    @Override
    public String deleteCategory(Long id) {

        log.info(
                "Delete requested for category ID : {}",
                id);

        List<SaleItem> soldItems =
                itemRepo.findAll();

        boolean billed =
                soldItems.stream()
                        .anyMatch(item ->
                                item.getProduct() != null
                                        &&
                                        item.getProduct()
                                                .getCategory() != null
                                        &&
                                        item.getProduct()
                                                .getCategory()
                                                .getId()
                                                .equals(id));

        if (billed) {

            log.warn(
                    "Attempted delete on billed category ID : {}",
                    id);

            return "Category already used in billing. Cannot delete.";
        }

        Category c =
                categoryRepo.findById(id)
                        .orElseThrow(() -> {

                            log.error(
                                    "Category not found for delete : {}",
                                    id);

                            return new RuntimeException(
                                    "Category not found");
                        });

        log.info(
                "Deleting category : {}",
                c.getName());

        categoryRepo.delete(c);

        log.info(
                "Category deleted successfully : {}",
                c.getName());

        return "Deleted successfully";
    }

    // SUPPLIER

    @Override
    public List<Supplier> getSuppliers() {

        log.info("Fetching suppliers");

        List<Supplier> suppliers =
                supplierRepo.findByTenant_IdAndStatus(
                        TenantContext.getTenantId(),
                        "ACTIVE");

        log.info(
                "Total suppliers fetched : {}",
                suppliers.size());

        return suppliers;
    }

    @Override
    public Supplier addSupplier(Supplier s) {

        log.info(
                "Adding supplier : {}",
                s.getName());

        Tenant tenant =
                tenantRepo.findById(
                                TenantContext.getTenantId())
                        .orElseThrow();

        s.setTenant(tenant);

        s.setStatus("ACTIVE");

        s.setSupplierCode(
                generateSupplierCode(
                        tenant.getId()));

        Supplier saved =
                supplierRepo.save(s);

        log.info(
                "Supplier created : {} | {}",
                saved.getSupplierCode(),
                saved.getName());

        return saved;
    }
    @Override
    public Supplier updateSupplier(
            Long id,
            Supplier body) {

        log.info(
                "Updating supplier ID : {}",
                id);

        Supplier s =
                supplierRepo.findById(id)
                        .orElseThrow(() -> {

                            log.error(
                                    "Supplier not found : {}",
                                    id);

                            return new RuntimeException(
                                    "Supplier not found");
                        });

        s.setName(body.getName());

        Supplier updated =
                supplierRepo.save(s);

        log.info(
                "Supplier updated successfully : {}",
                updated.getName());

        return updated;
    }

    @Override
    public String deleteSupplier(Long id) {

        log.info(
                "Deactivating supplier ID : {}",
                id);

        Supplier s =
                supplierRepo.findById(id)
                        .orElseThrow(() -> {

                            log.error(
                                    "Supplier not found for delete : {}",
                                    id);

                            return new RuntimeException(
                                    "Supplier not found");
                        });

        s.setStatus("INACTIVE");

        supplierRepo.save(s);

        log.info(
                "Supplier deactivated successfully : {}",
                s.getName());

        return "Supplier deactivated";
    }

    // CUSTOMER

    @Override
    public List<Customer> getCustomers() {

        log.info("Fetching customers");

        List<Customer> customers =
                customerRepo.findByTenant_IdAndStatus(
                        TenantContext.getTenantId(),
                        "ACTIVE");

        log.info(
                "Total customers fetched : {}",
                customers.size());

        return customers;
    }

    @Override
    public Customer addCustomer(Customer c) {

        log.info(
                "Adding customer : {}",
                c.getName());

        Tenant tenant =
                tenantRepo.findById(
                                TenantContext.getTenantId())
                        .orElseThrow();

        c.setTenant(tenant);

        c.setStatus("ACTIVE");

        c.setCustomerCode(
                generateCustomerCode(
                        tenant.getId()));

        Customer saved =
                customerRepo.save(c);

        log.info(
                "Customer added successfully : {}",
                saved.getName());

        return saved;
    }

    @Override
    public Customer updateCustomer(
            Long id,
            Customer body) {

        log.info(
                "Updating customer ID : {}",
                id);

        Customer c =
                customerRepo.findById(id)
                        .orElseThrow(() -> {

                            log.error(
                                    "Customer not found : {}",
                                    id);

                            return new RuntimeException(
                                    "Customer not found");
                        });

        c.setName(body.getName());

        Customer updated =
                customerRepo.save(c);

        log.info(
                "Customer updated successfully : {}",
                updated.getName());

        return updated;
    }

    @Override
    public void deleteCustomer(Long id) {

        log.info(
                "Deleting customer ID : {}",
                id);

        Customer c =
                customerRepo.findById(id)
                        .orElseThrow(() -> {

                            log.error(
                                    "Customer not found for delete : {}",
                                    id);

                            return new RuntimeException(
                                    "Customer not found");
                        });

        customerRepo.delete(c);

        log.info(
                "Customer deleted successfully : {}",
                c.getName());
    }

    @Override
    public List<SubCategory> getSubCategories() {

        return subCategoryRepo.findByTenant_IdAndStatus(
                TenantContext.getTenantId(),
                "ACTIVE");
    }

    @Override
    public SubCategory addSubCategory(
            Long categoryId,
            String name) {

        Category category =
                categoryRepo.findById(categoryId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found"));

        Tenant tenant =
                tenantRepo.findById(
                                TenantContext.getTenantId())
                        .orElseThrow();

        SubCategory subCategory =
                new SubCategory();

        subCategory.setName(name);

        subCategory.setCategory(category);

        subCategory.setTenant(tenant);

        subCategory.setStatus("ACTIVE");

        return subCategoryRepo.save(subCategory);
    }

    @Override
    public String deleteSubCategory(Long id) {

        SubCategory subCategory =
                subCategoryRepo.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Sub Category not found"));

        subCategoryRepo.delete(subCategory);

        return "Deleted successfully";
    }

    @Override
    public List<ShelfLife> getShelfLives() {

        return shelfLifeRepo.findByTenant_IdAndStatus(
                TenantContext.getTenantId(),
                "ACTIVE");
    }

    @Override
    public String deleteShelfLife(Long id) {
        ShelfLife shelfLife = shelfLifeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Shelf Life not found"));
        shelfLifeRepo.delete(shelfLife);
        return "Deleted successfully";
    }

    private String generateSupplierCode(
            Long tenantId) {

        Optional<Supplier> lastSupplier =
                supplierRepo
                        .findTopByTenant_IdOrderByIdDesc(
                                tenantId);

        if (lastSupplier.isEmpty()) {
            return "SUP-000001";
        }

        String lastCode =
                lastSupplier.get()
                        .getSupplierCode();

        if (lastCode == null ||
                lastCode.isBlank()) {

            return "SUP-000001";
        }

        int sequence =
                Integer.parseInt(
                        lastCode.replace(
                                "SUP-",
                                "")
                );

        sequence++;

        return String.format(
                "SUP-%06d",
                sequence
        );
    }

    private String generateCustomerCode(
            Long tenantId) {

        Optional<Customer> lastCustomer =
                customerRepo
                        .findTopByTenant_IdOrderByIdDesc(
                                tenantId);

        if (lastCustomer.isEmpty()) {
            return "CUS-000001";
        }

        String lastCode =
                lastCustomer.get()
                        .getCustomerCode();

        if (lastCode == null ||
                lastCode.isBlank()) {

            return "CUS-000001";
        }

        int sequence =
                Integer.parseInt(
                        lastCode.replace(
                                "CUS-",
                                "")
                );

        sequence++;

        return String.format(
                "CUS-%06d",
                sequence
        );
    }
}