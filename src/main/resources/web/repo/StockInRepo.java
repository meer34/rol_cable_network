package com.hunter.web.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hunter.web.model.StockIn;

public interface StockInRepo extends JpaRepository<StockIn, Long>, JpaSpecificationExecutor<StockIn>{
	
	Page<StockIn> findAllByOrderByIdDesc(Pageable pageable);
	
	@Query("FROM StockIn si WHERE EXISTS (SELECT 1 FROM Product p WHERE p.stockIn = si.id AND p.name = :name AND p.size = :pSize AND p.colour = :colour AND p.brand = :brand )")
	Page<StockIn> findAllStockInsByProductDetails(Pageable pageable, String name, String pSize, String colour, String brand);
	
	@Query("FROM StockIn stockIn WHERE stockIn.synced = FALSE")
	List<StockIn> findAllNotSyncedData();
	
	@Modifying(clearAutomatically=true, flushAutomatically=true)
	@Transactional
	@Query("UPDATE StockIn si SET si.synced = TRUE, si.remoteId = :remoteId WHERE si.id = :id")
	void markAsSynced(Long id, Long remoteId);

}
