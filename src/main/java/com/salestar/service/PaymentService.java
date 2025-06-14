package com.salestar.service;

import com.salestar.dto.OrderSummaryDto;
import com.salestar.entity.Order;
import jakarta.transaction.Transactional;

import java.util.List;

public interface PaymentService {
    void purchaseProduct(Long productId, int quantity);

    List<OrderSummaryDto> getAllOrders();

//    void processPayment(Order order, String method);
}