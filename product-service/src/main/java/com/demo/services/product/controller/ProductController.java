package com.demo.services.product.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.demo.services.product.entity.Product;
import com.demo.services.product.exception.ResourceNotFoundException;
import com.demo.services.product.repository.ProductRepository;
import com.demo.services.product.repository.ProductService;

@RestController
public class ProductController {

	@Autowired
	private ProductRepository productRepo;

	@Autowired
	private ProductService productService;

	@GetMapping("/health-check")
	public String healthCheck() throws JSONException {
		JSONObject responseObj = new JSONObject();
		responseObj.put("status", 200);

		return responseObj.toString();
	}

	@GetMapping("/products/{id}")
	public ResponseEntity<Product> getProduct(@PathVariable Long id) {
		Product product = productRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Product.class.getName(), "id", id));

		return ResponseEntity.ok(product);
	}

	@GetMapping("/products/{id}/avaibility/{orderQuantity}")
	public Boolean avaibility(@PathVariable("id") Long id, @PathVariable("orderQuantity") Integer orderQuantity) {
		// Boolean tempNewA = productService.checkAvaibility(id, orderQuantity);
		if (productRepo.countProductsByQuantity(id, orderQuantity) == 1) {
			return true;
		}
		return false;
	}

	@PutMapping("/products/{id}/decreaseQuantity/{orderedQuantity}")
	public Boolean decreaseQuantity(@PathVariable Long id, @PathVariable Integer orderedQuantity) {
		return productService.decreaseQuantity(id, orderedQuantity);
	}

	@PatchMapping("/products/{id}")
	public ResponseEntity<String> updateQuantity(@RequestBody Map<String, Integer> updatesMap, @PathVariable Long id) {

		Integer newQuantity = updatesMap.get("quantity");
		int updatedRows = productRepo.updateQuantity(id, newQuantity);

		if (updatedRows == 1) {
			return ResponseEntity.ok("New quantity was successfully updated");
		}

		return ResponseEntity.badRequest().body("Cannot update new quantity");

	}

}
