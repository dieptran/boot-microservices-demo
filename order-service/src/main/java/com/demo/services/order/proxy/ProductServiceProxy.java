package com.demo.services.order.proxy;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.fasterxml.jackson.databind.JsonNode;

@FeignClient(name="product-service")
@RibbonClient(name="product-service")
public interface ProductServiceProxy {
	
	@GetMapping("/products/{id}")
	public JsonNode getProduct(@PathVariable("id") Long id);

	@GetMapping("/products/{id}/avaibility/{orderQuantity}")
	public JsonNode checkAvaibility(@PathVariable("id") Long id, @PathVariable("orderQuantity") Integer orderQuantity);
	
	@PutMapping("/products/{id}/decreaseQuantity/{orderedQuantity}")
	public JsonNode decreaseQuantity(@PathVariable Long id, @PathVariable Integer orderedQuantity);
}
