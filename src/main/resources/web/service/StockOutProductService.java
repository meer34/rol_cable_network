package com.hunter.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hunter.web.model.Product;
import com.hunter.web.model.StockOut;
import com.hunter.web.model.StockOutProduct;
import com.hunter.web.repo.StockOutProductRepo;

@Service
public class StockOutProductService {

	@Autowired private StockOutProductRepo stockOutProductRepo;

	public List<StockOutProduct> findAllNotSyncedData() {
		return stockOutProductRepo.findAllNotSyncedData();
	}

	public StockOutProduct saveRemoteData(StockOutProduct stockOutProduct) {
		stockOutProduct.setSynced(true);
		
		Long tempId = stockOutProduct.getId();
		if(stockOutProduct.getRemoteId() != null) stockOutProduct.setId(stockOutProduct.getRemoteId());
		else stockOutProduct.setId(0L);
		stockOutProduct.setRemoteId(tempId);
		
		StockOut stockOut = stockOutProduct.getStockOut();
		if(stockOut != null) {
			tempId = stockOut.getId();
			if(stockOut.getRemoteId() != null) stockOut.setId(stockOut.getRemoteId());
			else stockOut.setId(0L);
			stockOut.setRemoteId(tempId);
		}
		
		Product product = stockOutProduct.getProduct();
		if(product != null) {
			tempId = product.getId();
			if(product.getRemoteId() != null) product.setId(product.getRemoteId());
			else product.setId(0L);
			product.setRemoteId(tempId);
		}
		stockOutProduct.setStockInProduct(stockOutProduct.getProduct().getId());
		
		System.out.println("Saving StockOutProduct with id: " + stockOutProduct.getId() + " remote id: " + stockOutProduct.getRemoteId());
		return stockOutProductRepo.save(stockOutProduct);
	}

	public void markAsSynced(Long id, Long remoteId) {
		stockOutProductRepo.markAsSynced(id, remoteId);
	}
	
}
