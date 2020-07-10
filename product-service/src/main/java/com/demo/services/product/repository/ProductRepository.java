package com.demo.services.product.repository;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.demo.services.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Transactional
    @Query(value = "UPDATE Product p SET p.quantity = :quantity WHERE p.id = :id", nativeQuery = false)
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
	
	@Query("SELECT COUNT(p.id) from Product p where p.id = :id and p.quantity >= :quantity")
	@Transactional
	@Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    long countProductsByQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
	
    @Query("select p from Product p where p.id = :id")
    Product getProduct(@Param("id") Long id);
}
