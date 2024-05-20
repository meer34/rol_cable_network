package com.hunter.web.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hunter.web.model.StockOutProduct;

public interface StockOutProductRepo extends JpaRepository<StockOutProduct, Long>{
	
	@Query("FROM StockOutProduct sop where sop.stockOut = (FROM StockOut so where so.id = :stockOutId) AND sop.id NOT IN :currentChildIds")
	List<StockOutProduct> getStockOutOrphanChilds(long stockOutId, List<Long> currentChildIds);

	@Query("FROM StockOutProduct sp WHERE sp.synced = FALSE")
	List<StockOutProduct> findAllNotSyncedData();
	
	@Modifying(clearAutomatically=true, flushAutomatically=true)
	@Transactional
	@Query("UPDATE StockOutProduct sp SET sp.synced = TRUE, sp.remoteId = :remoteId WHERE sp.id = :id")
	void markAsSynced(Long id, Long remoteId);


}
