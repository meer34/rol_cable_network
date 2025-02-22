package com.hunter.web.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hunter.web.model.Product;

public interface ProductRepo extends JpaRepository<Product, Long>{

	@Query("SELECT DISTINCT name FROM Product")
	List<String> findProductNames();

	@Query("SELECT DISTINCT size FROM Product where name = :name")
	List<String> findSizesForName(String name);

	@Query("SELECT DISTINCT colour FROM Product where name = :name AND size = :size")
	List<String> findColoursForNameAndSize(String name,String size);

	@Query("SELECT id, brand FROM Product where name = :name AND size = :size AND colour = :colour")
	List<String[]> findBrandsForNameAndSizeAndColour(String name,String size, String colour);

//	@Query("SELECT p.quantity - COALESCE(SUM(Product.stockOutProductList.quantity), 0) FROM Product p WHERE p.id = :productId")
//	String findQuantityForProductId(Long productId);

	@Query("FROM Product p where p.stockIn = (FROM StockIn si where si.id = :stockInId) AND p.id NOT IN :currentChildIds "
			+ "AND NOT EXISTS (SELECT 1 FROM StockOutProduct sop where sop.stockInProduct = p.id)")
	List<Product> getStockInOrphanChilds(Long stockInId, List<Long> currentChildIds);

	Product findFirstByScanCodeOrderByIdDesc(String scanCode);
	
	@Query("FROM Product p WHERE p.synced = FALSE")
	List<Product> findAllNotSyncedData();
	
	@Modifying(clearAutomatically=true, flushAutomatically=true)
	@Transactional
	@Query("UPDATE Product p SET p.synced = TRUE, p.remoteId = :remoteId WHERE p.id = :id")
	void markAsSynced(Long id, Long remoteId);
	
	@Query("FROM Product p where p.stockIn = (FROM StockIn si where si.id = :stockInId)")
	List<Product> findAllByStockInId(Long stockInId);


}
