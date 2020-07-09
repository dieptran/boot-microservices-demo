package com.demo.services.product.repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.services.product.entity.Product;

@Service
public class ProductService {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private ProductRepository productRepo;
	
	@Transactional
	public Boolean checkAvaibility(final Long id, final Integer quantity) {
		Query query = entityManager.createQuery("SELECT COUNT(p.id) from Product p where p.id = :id and p.quantity >= :quantity");
		query.setParameter("id", id);
		query.setParameter("quantity", quantity);
		query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
		return Integer.valueOf(query.getResultList().get(0).toString()) > 0;
	}
	
	@Transactional
	public Boolean decreaseQuantity(final Long id, final Integer orderQuantity) {
//		entityManager.getTransaction().begin();
//		Product product = entityManager.find(Product.class, id, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//		product.setQuantity(product.getQuantity() - orderQuantity);
//		entityManager.persist(product);
//		entityManager.getTransaction().commit();
//		entityManager.close();
		
		Product product = productRepo.findById(id).get();
		Integer newQuantity = product.getQuantity() - orderQuantity;
		int updatedRow = productRepo.updateQuantity(id, newQuantity);
		
		if(updatedRow == 1) {
			return true;
		}
		
		return false;
	}
}
