package com.demo.services.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.services.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
