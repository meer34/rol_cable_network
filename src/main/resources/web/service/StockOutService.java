package com.hunter.web.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.hunter.web.model.Customer;
import com.hunter.web.model.StockOut;
import com.hunter.web.repo.ProductRepo;
import com.hunter.web.repo.StockOutProductRepo;
import com.hunter.web.repo.StockOutRepo;
import com.hunter.web.specification.StockOutSearchSpecification;
import com.hunter.web.util.SearchSpecificationBuilder;

@Service
public class StockOutService {

	@Autowired private StockOutRepo stockOutRepo;
	@Autowired private ProductRepo rollRepo;
	@Autowired private StockOutProductRepo sopRepo;

	public StockOut saveStockOutToDB(StockOut stockOut) {
		stockOut.processParts(rollRepo, sopRepo);
		return stockOutRepo.save(stockOut);
	}

	public StockOut findStockOutById(Long id) {
		return stockOutRepo.findById(id).get();
	}

	public Page<StockOut> getAllStockOuts(Integer pageNo, Integer pageSize) {
		return stockOutRepo.findAllByOrderByIdDesc(PageRequest.of(pageNo, pageSize));
	}
	
	public Page<StockOut> getAllStockOutsForProduct(String name, String pSize, String colour, String brand, Integer pageNo, Integer pageSize) {
		return stockOutRepo.findAllStockOutsByProductDetails(PageRequest.of(pageNo, pageSize), name,pSize, colour, brand);
	}
	
	public Page<StockOut> getAllStockOutsForCustomer(Long custId, Integer pageNo, Integer pageSize) {
		return stockOutRepo.findAllStockOutsByCustomerId(PageRequest.of(pageNo, pageSize), custId);
	}

	public Page<StockOut> searchStockOutByDateAndKeyword(String keyword, 
			String fromDate, String toDate, int pageNo, Integer pageSize) throws ParseException {

		PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
		StockOutSearchSpecification spec = (StockOutSearchSpecification) SearchSpecificationBuilder.build(fromDate, toDate, keyword, StockOut.class);
		return stockOutRepo.findAll(spec, pageRequest);

	}

	public void deleteStockOutById(Long id) {
		stockOutRepo.deleteById(id);
	}
	
	public List<StockOut> findAllNotSyncedData() {
		return stockOutRepo.findAllNotSyncedData();
	}

	public StockOut saveRemoteData(StockOut stockOut) {
		stockOut.setSynced(true);
		
		Long tempId = stockOut.getId();
		if(stockOut.getRemoteId() != null) stockOut.setId(stockOut.getRemoteId());
		else stockOut.setId(0L);
		stockOut.setRemoteId(tempId);
		
		Customer customer = stockOut.getCustomer();
		if(customer != null) {
			tempId = customer.getId();
			if(customer.getRemoteId() != null) customer.setId(customer.getRemoteId());
			else customer.setId(0L);
			customer.setRemoteId(tempId);
		}
		
		System.out.println("Saving StockOut with id: " + stockOut.getId() + " remote id: " + stockOut.getRemoteId());
		return stockOutRepo.save(stockOut);
	}

	public void markAsSynced(Long id, Long remoteId) {
		stockOutRepo.markAsSynced(id, remoteId);
	}

}
