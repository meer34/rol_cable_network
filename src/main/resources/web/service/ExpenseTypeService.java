package com.hunter.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hunter.web.model.ExpenseType;
import com.hunter.web.repo.ExpenseTypeRepo;

@Service
public class ExpenseTypeService {

	@Autowired private ExpenseTypeRepo expenseTypeRepo;

	public List<ExpenseType> findAllNotSyncedData() {
		return expenseTypeRepo.findAllNotSyncedData();
	}

	public ExpenseType saveRemoteData(ExpenseType expenseType) {
		expenseType.setSynced(true);
		
		Long tempId = expenseType.getId();
		if(expenseType.getRemoteId() != null) expenseType.setId(expenseType.getRemoteId());
		else expenseType.setId(0L);
		expenseType.setRemoteId(tempId);
		
		System.out.println("Saving ExpenseType with id: " + expenseType.getId() + " remote id: " + expenseType.getRemoteId());
		return expenseTypeRepo.saveAndFlush(expenseType);
	}

	public void markAsSynced(Long id, Long serverId) {
		expenseTypeRepo.markAsSynced(id, serverId);
	}
	
}
