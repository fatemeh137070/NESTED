package com.salestar.service;

import com.salestar.entity.Order;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentTransactionService {
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void processPayment(Order order, String method);
}
