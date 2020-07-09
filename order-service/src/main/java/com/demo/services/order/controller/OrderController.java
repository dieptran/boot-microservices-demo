package com.demo.services.order.controller;

import java.net.URI;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.demo.services.order.dto.OrderRequest;
import com.demo.services.order.entity.Order;
import com.demo.services.order.service.OrderService;

@RestController
@Validated
public class OrderController {

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired OrderService orderService;

	@GetMapping("/health-check")
	public ResponseEntity<String> healthCheck() {
		return ResponseEntity.ok().body("{status: \"ok\"}");
	}
	
	@GetMapping("/orders/{id}")
	public ResponseEntity<Order> getOrder(@PathVariable Long id) {
		log.debug("Getting order: id=", id);
		Order order = orderService.getOrderById(id);
		return ResponseEntity.ok(order);
	}

	@PostMapping("/orders")
	public ResponseEntity<String> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
		log.debug("Placing order: {}", orderRequest);
		Order newOrder = orderService.placeOrder(orderRequest);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(newOrder.getId()).toUri();

		return ResponseEntity.created(location).body("Order was successfully created");
	}

}
