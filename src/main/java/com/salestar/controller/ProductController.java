package com.salestar.controller;


import com.salestar.dto.ProductDto;
import com.salestar.facade.ProductFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductFacade productFacade;

    @PostMapping
    public ProductDto addProduct(@RequestBody ProductDto dto) {
        return productFacade.addProduct(dto);
    }

    @GetMapping
    public List<ProductDto> getAll() {
        return productFacade.listAllProducts();
    }
}