package com.salestar.controller;

import com.salestar.dto.OrderSummaryDto;
import com.salestar.entity.Order;
import com.salestar.entity.PaymentTransaction;
import com.salestar.service.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentServiceImpl paymentService;


    @PostMapping("/purchase")
    public ResponseEntity<String> purchase(@RequestParam Long productId,
                                           @RequestParam int quantity) {
        try {
            paymentService.purchaseProduct(productId, quantity);
            return ResponseEntity.ok("Purchase successful");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }

    @GetMapping
    public List<OrderSummaryDto> getAllOrders() {
       return paymentService.getAllOrders();
    }
}
