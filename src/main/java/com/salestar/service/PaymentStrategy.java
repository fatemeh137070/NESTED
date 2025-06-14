package com.salestar.service;

import com.salestar.entity.Order;

public interface PaymentStrategy {
    void pay(Order order);
}