package com.hunter.web.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hunter.web.model.App User;
import com.hunter.web.model.User;

public interface App UserRepo extends JpaRepository<App User, Long>{

	List<App User> findByPhoneAndIdNot(String phone, Long id);
	
	@Query("FROM App User m WHERE m.user = :user")
	Optional<App User> findByUser(User user);
	
	@Query("FROM App User m WHERE m.synced = FALSE")
	List<App User> findAllNotSyncedData();
	
	@Modifying(clearAutomatically=true, flushAutomatically=true)
	@Transactional
	@Query("UPDATE App User m SET m.synced = TRUE, m.remoteId = :remoteId WHERE m.id = :id")
	void markAsSynced(Long id, Long remoteId);
	
}
