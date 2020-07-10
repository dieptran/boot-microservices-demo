package com.demo.services.product.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.services.product.entity.Product;
import com.demo.services.product.exception.ResourceNotFoundException;
import com.demo.services.product.repository.ProductRepository;

@Service
public class ProductService {

	private static final Logger log = LoggerFactory.getLogger(ProductService.class);

	@Autowired
	ProductRepository productRepo;

	/**
	 * Get product by id
	 * 
	 * @param productId
	 * @return
	 */
	public Product getProductById(Long productId) {
		log.debug("Getting product: id=" + productId);
		Product product = productRepo.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

		return product;
	}

	/**
	 * Check if quantity in inventory is enough for the ordered quantity
	 * 
	 * @param id
	 * @param orderQuantity
	 * @return
	 */
	public boolean avaibility(Long productId, Integer orderQuantity) {
		log.debug("Checking avaibility for product id = " + productId);
		return (productRepo.countProductsByQuantity(productId, orderQuantity) == 1);
	}

	/**
	 * Update quantity in inventory by subtracting a ordered quantity
	 * 
	 * @param productId
	 * @param orderedQuantity
	 * @return true / false
	 */
	public boolean decreaseQuantity(Long productId, Integer orderedQuantity) {
		log.debug("Descreasing quantity in inventory: productId={} with orderedQuantity={}", productId, orderedQuantity);
		Product product = productRepo.findById(productId).get();
		Integer newQuantity = product.getQuantity() - orderedQuantity;

		int updatedRow = productRepo.updateQuantity(productId, newQuantity);
		if (updatedRow == 1) {
			return true;
		}

		return false;
	}
	
	/**
	 * Find all product
	 * @return
	 */
	public List<Product> getProducts() {
		return productRepo.findAll();
	}

}
