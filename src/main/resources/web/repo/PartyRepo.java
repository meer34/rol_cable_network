package com.hunter.web.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hunter.web.model.Party;

public interface PartyRepo extends JpaRepository<Party, Long>{
	
	@Query("FROM Party p WHERE p.synced = FALSE")
	List<Party> findAllNotSyncedData();
	
	@Modifying(clearAutomatically=true, flushAutomatically=true)
	@Transactional
	@Query("UPDATE Party p SET p.synced = TRUE, p.remoteId = :remoteId WHERE p.id = :id")
	void markAsSynced(Long id, Long remoteId);
	
}
