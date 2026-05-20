package com.inventory.management.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventory.management.dto.ProductDto;
import com.inventory.management.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.name", target = "categoryName")
    ProductDto toDto(Product product);

    List<ProductDto> toDtoList(List<Product> products);
}