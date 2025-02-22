package com.hunter.web.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hunter.web.model.Reminder;

public interface ReminderRepo extends JpaRepository<Reminder, Long>, JpaSpecificationExecutor<Reminder>{

	@Query("FROM Reminder reminder WHERE reminder.status <> 'Sent' AND reminder.date = CURRENT_DATE")
	List<Reminder> findAllPendingRemindersForToday();
	
	@Query("FROM Reminder r WHERE r.synced = FALSE")
	List<Reminder> findAllNotSyncedData();
	
	@Modifying(clearAutomatically=true, flushAutomatically=true)
	@Transactional
	@Query("UPDATE Reminder r SET r.synced = TRUE, r.remoteId = :remoteId WHERE r.id = :id")
	void markAsSynced(Long id, Long remoteId);

}
