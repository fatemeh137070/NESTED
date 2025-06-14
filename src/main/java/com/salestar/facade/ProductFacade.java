package com.salestar.facade;


import com.salestar.dto.ProductDto;
import com.salestar.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ProductFacade {

    @Autowired
    private ProductService productService;

    public ProductDto addProduct(ProductDto dto) {
        return productService.createProduct(dto);
    }

    public List<ProductDto> listAllProducts() {
        return productService.getAllProducts();
    }
}