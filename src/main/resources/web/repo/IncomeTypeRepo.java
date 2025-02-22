package com.hunter.web.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hunter.web.model.IncomeType;

public interface IncomeTypeRepo extends JpaRepository<IncomeType, Long>{
	
	@Query("FROM IncomeType it WHERE it.synced = FALSE")
	List<IncomeType> findAllNotSyncedData();
	
	@Modifying(clearAutomatically=true, flushAutomatically=true)
	@Transactional
	@Query("UPDATE IncomeType it SET it.synced = TRUE, it.remoteId = :remoteId WHERE it.id = :id")
	void markAsSynced(Long id, Long remoteId);
	
}
