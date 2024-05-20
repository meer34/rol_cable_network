package com.hunter.web.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.hunter.web.model.Party;
import com.hunter.web.model.Product;
import com.hunter.web.model.StockIn;
import com.hunter.web.model.StockOutProduct;
import com.hunter.web.repo.ProductRepo;
import com.hunter.web.repo.StockInRepo;
import com.hunter.web.specification.StockInSearchSpecification;
import com.hunter.web.util.SearchSpecificationBuilder;

@Service
public class StockInService {

	@Autowired private StockInRepo stockInRepo;
	@Autowired private ProductRepo productRepo;

	public StockIn saveStockInToDB(StockIn stockIn) {
		stockIn.setSynced(false);
		stockIn.processParts(productRepo);
		return stockInRepo.save(stockIn);
	}

	public Page<StockIn> getAllStockIns(Integer pageNo, Integer pageSize) {
		return stockInRepo.findAllByOrderByIdDesc(PageRequest.of(pageNo, pageSize));
	}

	public Page<StockIn> getAllStockInsForProduct(String name, String pSize, String colour, String brand, Integer pageNo, Integer pageSize) {
		return stockInRepo.findAllStockInsByProductDetails(PageRequest.of(pageNo, pageSize), name,pSize, colour, brand);
	}

	public List<String> getAllStockInProductNames() {
		return productRepo.findProductNames();
	}

	public List<String> getSizesForProductName(String productName) {
		return productRepo.findSizesForName(productName);
	}

	public List<String> getColoursForNameAndSize(String name, String size) {
		return productRepo.findColoursForNameAndSize(name, size);
	}

	public List<String[]> getBrandsForNameAndSizeAndColour(String name, String size, String colour) {
		return productRepo.findBrandsForNameAndSizeAndColour(name, size, colour);
	}

	public Integer getQuantityForProductId(Long productId) {
		Product product = productRepo.findById(productId).orElse(null);
		
		if(product == null) return 0;
		
		Integer quantity = product.getQuantity();
		for (StockOutProduct stockOutProduct : product.getStockOutProductList()) {
			quantity -= stockOutProduct.getQuantity();
		}
		return quantity;
		
	}

	public StockIn findStockInById(long id) {
		return (StockIn) stockInRepo.findById(id).get();
	}

	public Page<StockIn> searchStockInByDateAndKeyword(String keyword, 
			String fromDate, String toDate, int pageNo, Integer pageSize) throws ParseException {

		PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
		StockInSearchSpecification spec = (StockInSearchSpecification) SearchSpecificationBuilder.build(fromDate, toDate, keyword, StockIn.class);
		return stockInRepo.findAll(spec, pageRequest);

	}

	public void deleteStockInById(Long id) {
		stockInRepo.deleteById(id);
	}

	public Product findStockInByScanCode(String scanCode) {
		return productRepo.findFirstByScanCodeOrderByIdDesc(scanCode);
	}

	public Product findAvailableQuantityForProduct(String scanCode) {
		return productRepo.findFirstByScanCodeOrderByIdDesc(scanCode);
	}

	public List<StockIn> findAllNotSyncedData() {
		return stockInRepo.findAllNotSyncedData();
	}

	public StockIn saveRemoteData(StockIn stockIn) {
		stockIn.setSynced(true);

		Long tempId = stockIn.getId();
		if(stockIn.getRemoteId() != null) stockIn.setId(stockIn.getRemoteId());
		else stockIn.setId(0L);
		stockIn.setRemoteId(tempId);
		
		Party party = stockIn.getMahajan();
		if(party != null) {
			Long tempPartyId = party.getId();
			if(party.getRemoteId() != null) party.setId(party.getRemoteId());
			else party.setId(0L);
			party.setRemoteId(tempPartyId);
		}

		return stockInRepo.save(stockIn);
	}

	public void markAsSynced(Long id, Long serverId) {
		stockInRepo.markAsSynced(id, serverId);
	}

}
