package com.hunter.web.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hunter.web.model.Customer;

public interface CustomerRepo extends JpaRepository<Customer, Long>{

	@Query("FROM Customer c WHERE c.synced = FALSE")
	List<Customer> findAllNotSyncedData();
	
	@Modifying(clearAutomatically=true, flushAutomatically=true)
	@Transactional
	@Query("UPDATE Customer c SET c.synced = TRUE, c.remoteId = :remoteId WHERE c.id = :id")
	void markAsSynced(Long id, Long remoteId);
	
}
