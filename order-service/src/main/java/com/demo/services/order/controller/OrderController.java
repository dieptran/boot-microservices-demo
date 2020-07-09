package com.demo.services.order.controller;

import java.net.URI;
import java.time.Instant;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.demo.services.order.proxy.ProductServiceProxy;
import com.demo.services.order.repository.OrderRepository;
import com.demo.services.order.util.AppUtil;

@RestController
@Validated
public class OrderController {

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private OrderRepository orderRepo;

	@Autowired
	private ProductServiceProxy productProxy;

	@GetMapping("/health-check")
	public ResponseEntity<String> healthCheck() {
		return ResponseEntity.ok().body("{status: \"ok\"}");
	}
	
	@GetMapping("/orders/{id}")
	public ResponseEntity<Order> getOrder(@PathVariable Long id) {
		Order order = orderRepo.findById(id).get();
		return ResponseEntity.ok(order);
	}

	@PostMapping("/orders")
	public ResponseEntity<String> createOrder(@Valid @RequestBody OrderRequest orderRequest) {

		String customerEmail = orderRequest.getCustomerEmail();
		Long productId = orderRequest.getProductId();
		int orderedQuantity = orderRequest.getQuantity();
		double price = orderRequest.getPrice();

		Boolean availableToPlace = productProxy.checkAvaibility(productId, orderedQuantity).asBoolean();
		if (!availableToPlace) {
			return ResponseEntity.badRequest().body("Not enough quantity to place order");
		}

		double subtotal = price * orderedQuantity;
		Order order = new Order();
		order.setCode("O-" + AppUtil.getRandomNumber());
		order.setCustomerEmail(customerEmail);
		order.setStatus(Order.OrderStatus.CONFIRMED);
		order.setSubTotal(subtotal);
		order.setTotal(subtotal);
		order.setOrderDate(Instant.now());

		Order createdOrder = orderRepo.save(order);

		Boolean updatedQuantityInProduct = productProxy.decreaseQuantity(productId, orderedQuantity).asBoolean();
		if (!updatedQuantityInProduct) {
			// rollback order
			log.debug("Rollback by removing created order due to failed update quantity in product");
			orderRepo.delete(createdOrder);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknow error during placing order");
		}

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(createdOrder.getId()).toUri();

		return ResponseEntity.created(location).body("Order was successfully created");
	}

}
