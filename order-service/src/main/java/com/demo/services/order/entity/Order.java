package com.demo.services.order.entity;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "orders")
public class Order {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	public static enum OrderStatus {
		DRAFT, PENDING, CONFIRMED, SHIPPING, DELIVERED
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "code", nullable = false)
	private String code;

	@Column(name = "orderDate")
	@CreatedDate
	private Instant orderDate;

	@Column(name = "subTotal")
	private Double subTotal = 0.0;

	@Column(name = "tax")
	private Double tax = 0.0;

	@Column(name = "discount")
	private Double discount = 0.0;

	@Column(name = "total", nullable = false)
	private Double total = 0.0;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@NotNull(message = "Customer email cannot be null")
	@Email(message = "Customer email should be valid")
	@Column(name = "customerEmail", nullable = false)
	private String customerEmail;

	public Order() {
		// no-arg constructor for hibernate 3.5 loading
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Instant getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Instant orderDate) {
		this.orderDate = orderDate;
	}

	public Double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(Double subTotal) {
		this.subTotal = subTotal;
	}

	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", code=" + code + ", orderDate=" + orderDate + ", subTotal=" + subTotal + ", tax="
				+ tax + ", discount=" + discount + ", total=" + total + ", status=" + status + ", customerEmail="
				+ customerEmail + "]";
	}

}
