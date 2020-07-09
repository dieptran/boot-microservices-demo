package com.demo.services.order.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.codehaus.stax2.ri.typed.ValueDecoderFactory.DecimalDecoder;

@SuppressWarnings("unused")
public class OrderRequest {
	
	@NotNull(message = "Customer email cannot be null")
	@Email(message = "Customer email should be valid")
	private String customerEmail;
	
	@NotNull(message = "Product Id cannot be null")
	private Long productId;
	
	@NotNull(message = "Product quantity cannot be null")
	@Min(value = 1, message = "Quantity should not be less than 1")
	private int quantity;
	
	private double price; 

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "OrderRequest [customerEmail=" + customerEmail + ", productId=" + productId + ", productQuantity="
				+ quantity + "]";
	}
	
}
