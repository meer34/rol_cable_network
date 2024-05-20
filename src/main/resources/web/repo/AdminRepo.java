package com.hunter.web.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hunter.web.model.Admin;
import com.hunter.web.model.User;

public interface AdminRepo extends JpaRepository<Admin, Long>{

	List<Admin> findByPhoneAndIdNot(String phone, Long id);
	
	@Query("FROM Admin adm WHERE adm.user = :user")
	Optional<Admin> findByUser(User user);
	
	@Query("FROM Admin adm WHERE adm.synced = FALSE")
	List<Admin> findAllNotSyncedData();
	
	@Modifying(clearAutomatically=true, flushAutomatically=true)
	@Transactional
	@Query("UPDATE Admin adm SET adm.synced = TRUE, adm.remoteId = :remoteId WHERE adm.id = :id")
	void markAsSynced(Long id, Long remoteId);
	
}
