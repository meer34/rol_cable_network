package com.hunter.web.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hunter.web.model.Income;

public interface IncomeRepo extends JpaRepository<Income, Long>, JpaSpecificationExecutor<Income> {
	
	@Query("FROM Income income WHERE income.incomeType = (FROM IncomeType incomeType WHERE incomeType.id = :incomeTypeId)")
	Page<Income> findAllForType(Pageable pageable, Long incomeTypeId);
	
	@Query("FROM Income inc WHERE inc.synced = FALSE")
	List<Income> findAllNotSyncedData();
	
	@Modifying(clearAutomatically=true, flushAutomatically=true)
	@Transactional
	@Query("UPDATE Income inc SET inc.synced = TRUE, inc.remoteId = :remoteId WHERE inc.id = :id")
	void markAsSynced(Long id, Long remoteId);

}
