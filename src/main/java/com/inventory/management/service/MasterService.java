package com.inventory.management.service;

import com.inventory.management.entity.Category;
import com.inventory.management.entity.Customer;
import com.inventory.management.entity.SubCategory;
import com.inventory.management.entity.Supplier;
import com.inventory.management.entity.ShelfLife;
import com.inventory.management.entity.GstMaster;
import java.util.List;
import java.util.Map;

public interface MasterService {

    List<Map<String, Object>> getCategories();

    Category addCategory(Category c);

    Category updateCategory(Long id, Category body);

    String deleteCategory(Long id);

    List<Supplier> getSuppliers();

    Supplier addSupplier(Supplier s);

    Supplier updateSupplier(Long id, Supplier body);

    String deleteSupplier(Long id);

    List<Customer> getCustomers();

    Customer addCustomer(Customer c);

    Customer updateCustomer(Long id, Customer body);

    void deleteCustomer(Long id);

    List<SubCategory> getSubCategories();

    SubCategory addSubCategory(
            Long categoryId,
            String name);

    String deleteSubCategory(Long id);

    List<ShelfLife> getShelfLives();


    String deleteShelfLife(Long id);


    ShelfLife addShelfLife(ShelfLife shelfLife, String mappingType, List<Long> mappingIds);

    List<Map<String, Object>> getShelfLivesWithMappings();

    List<Map<String, Object>> getGsts();

    String deleteGst(Long id);

    GstMaster addGst(
            GstMaster gst,
            String mappingType,
            List<Long> mappingIds);

}