package com.hunter.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hunter.web.model.Product;
import com.hunter.web.model.StockIn;
import com.hunter.web.repo.ProductRepo;

@Service
public class ProductService {

	@Autowired private ProductRepo productRepo;

	public List<Product> findAllNotSyncedData() {
		return productRepo.findAllNotSyncedData();
	}

	public Product saveRemoteData(Product product) {
		product.setSynced(true);
		
		Long tempId = product.getId();
		if(product.getRemoteId() != null) product.setId(product.getRemoteId());
		else product.setId(0L);
		product.setRemoteId(tempId);
		
		StockIn stockIn = product.getStockIn();
		if(stockIn != null) {
			tempId = stockIn.getId();
			if(stockIn.getRemoteId() != null) stockIn.setId(stockIn.getRemoteId());
			else stockIn.setId(0L);
			stockIn.setRemoteId(tempId);
		}
		
		System.out.println("Saving Product with id: " + product.getId() + " remote id: " + product.getRemoteId());
		return productRepo.save(product);
	}

	public void markAsSynced(Long id, Long serverId) {
		productRepo.markAsSynced(id, serverId);
	}
	
}
