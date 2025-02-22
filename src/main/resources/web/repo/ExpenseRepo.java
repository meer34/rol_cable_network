package com.hunter.web.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hunter.web.model.Expense;

public interface ExpenseRepo extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {
	
	@Query("FROM Expense expense WHERE expense.expenseType = (FROM ExpenseType expenseType WHERE expenseType.id = :expenseTypeId)")
	Page<Expense> findAllForType(Pageable pageable, Long expenseTypeId);
	
	@Query("FROM Expense ep WHERE ep.synced = FALSE")
	List<Expense> findAllNotSyncedData();
	
	@Modifying(clearAutomatically=true, flushAutomatically=true)
	@Transactional
	@Query("UPDATE Expense ep SET ep.synced = TRUE, ep.remoteId = :remoteId WHERE ep.id = :id")
	void markAsSynced(Long id, Long remoteId);

}
