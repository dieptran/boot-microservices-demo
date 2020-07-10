package com.demo.services.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.services.product.entity.Product;
import com.demo.services.product.service.ProductService;

@RestController
public class ProductController {

	@Autowired
	private ProductService productService;

	@GetMapping("/health-check")
	public ResponseEntity<String> healthCheck() throws JSONException {
		return ResponseEntity.ok("alive");
	}
	
	@GetMapping("/products")
	public ResponseEntity<List<Product>> getProducts() {
		List<Product> products = productService.getProducts();
		return ResponseEntity.ok(products);
	}

	@GetMapping("/products/{id}")
	public ResponseEntity<Product> getProduct(@PathVariable Long id) {
		Product product = productService.getProductById(id);
		return ResponseEntity.ok(product);
	}

	@GetMapping("/products/{id}/avaibility/{orderQuantity}")
	public ResponseEntity<Boolean> avaibility(@PathVariable("id") Long id, @PathVariable("orderQuantity") Integer orderQuantity) {
		Boolean isAvailable = productService.avaibility(id, orderQuantity);
		return ResponseEntity.ok(isAvailable);
	}

	@PutMapping("/products/{id}/decreaseQuantity/{orderedQuantity}")
	public Boolean decreaseQuantity(@PathVariable Long id, @PathVariable Integer orderedQuantity) {
		return productService.decreaseQuantity(id, orderedQuantity);
	}

}
