package com.salestar.controller;


import com.salestar.dto.ProductDto;
import com.salestar.facade.ProductFacade;
import com.salestar.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductFacade productFacade;

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ProductDto addProduct(@RequestBody ProductDto dto) {
        return productFacade.addProduct(dto);
    }

    @GetMapping
    public List<ProductDto> getAll() {
        return productFacade.listAllProducts();
    }

    @PostMapping("/buy/orchestration/{id}/{qty}")
    public void buyWithOrchestration(@PathVariable Long id, @PathVariable int qty) {
        productService.purchaseProductOrchestration(id, qty);
    }

    @PostMapping("/buy/choreography/{id}/{qty}")
    public void buyWithChoreography(@PathVariable Long id, @PathVariable int qty) {
        productService.purchaseProductChoreography(id, qty);
    }
}