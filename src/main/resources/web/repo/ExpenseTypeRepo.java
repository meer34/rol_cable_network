package com.hunter.web.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hunter.web.model.ExpenseType;

public interface ExpenseTypeRepo extends JpaRepository<ExpenseType, Long>{
	
	@Query("FROM ExpenseType et WHERE et.synced = FALSE")
	List<ExpenseType> findAllNotSyncedData();
	
	@Modifying(clearAutomatically=true, flushAutomatically=true)
	@Transactional
	@Query("UPDATE ExpenseType et SET et.synced = TRUE, et.remoteId = :remoteId WHERE et.id = :id")
	void markAsSynced(Long id, Long remoteId);
	
}
