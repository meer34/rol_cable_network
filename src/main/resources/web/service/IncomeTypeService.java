package com.hunter.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hunter.web.model.IncomeType;
import com.hunter.web.repo.IncomeTypeRepo;

@Service
public class IncomeTypeService {

	@Autowired private IncomeTypeRepo incomeTypeRepo;

	public List<IncomeType> findAllNotSyncedData() {
		return incomeTypeRepo.findAllNotSyncedData();
	}

	public IncomeType saveRemoteData(IncomeType incomeType) {
		
		incomeType.setSynced(true);
		
		Long tempIncomeTypeId = incomeType.getId();
		if(incomeType.getRemoteId() != null) incomeType.setId(incomeType.getRemoteId());
		else incomeType.setId(0L);
		incomeType.setRemoteId(tempIncomeTypeId);
		
		System.out.println("Saving IncomeType with id: " + incomeType.getId() + " remote id: " + incomeType.getRemoteId());
		return incomeTypeRepo.saveAndFlush(incomeType);
	}

	public void markAsSynced(Long id, Long serverId) {
		incomeTypeRepo.markAsSynced(id, serverId);
	}
	
}
