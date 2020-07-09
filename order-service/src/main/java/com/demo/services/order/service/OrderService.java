package com.demo.services.order.service;

import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.services.order.dto.OrderRequest;
import com.demo.services.order.entity.Order;
import com.demo.services.order.exception.AppException;
import com.demo.services.order.exception.BadRequestException;
import com.demo.services.order.exception.ResourceNotFoundException;
import com.demo.services.order.proxy.ProductServiceProxy;
import com.demo.services.order.repository.OrderRepository;
import com.demo.services.order.util.AppUtil;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepo;

	@Autowired
	private ProductServiceProxy productProxy;

	private static final Logger log = LoggerFactory.getLogger(OrderService.class);

	public Order getOrderById(Long orderId) {

		Optional<Order> orderOptional = orderRepo.findById(orderId);

		if (!orderOptional.isPresent()) {
			throw new ResourceNotFoundException("Order", "id", orderId);
		}

		return orderOptional.get();
	}

	public Order placeOrder(OrderRequest orderRequest) {

		String customerEmail = orderRequest.getCustomerEmail();
		Long productId = orderRequest.getProductId();
		int orderedQuantity = orderRequest.getQuantity();
		double price = orderRequest.getPrice();

		Boolean availableToPlace = productProxy.checkAvaibility(productId, orderedQuantity).asBoolean();
		if (!availableToPlace) {
			throw new BadRequestException("Not enough quantity to place order");
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
			throw new AppException("Unknow error during descrease quantity in inventory");
		}

		return createdOrder;
	}
}
