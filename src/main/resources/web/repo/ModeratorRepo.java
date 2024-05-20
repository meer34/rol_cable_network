package com.hunter.web.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hunter.web.model.Moderator;
import com.hunter.web.model.User;

public interface ModeratorRepo extends JpaRepository<Moderator, Long>{

	List<Moderator> findByPhoneAndIdNot(String phone, Long id);
	
	@Query("FROM Moderator m WHERE m.user = :user")
	Optional<Moderator> findByUser(User user);
	
	@Query("FROM Moderator m WHERE m.synced = FALSE")
	List<Moderator> findAllNotSyncedData();
	
	@Modifying(clearAutomatically=true, flushAutomatically=true)
	@Transactional
	@Query("UPDATE Moderator m SET m.synced = TRUE, m.remoteId = :remoteId WHERE m.id = :id")
	void markAsSynced(Long id, Long remoteId);
	
}
