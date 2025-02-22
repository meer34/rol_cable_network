package com.hunter.web.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.hunter.web.model.Income;
import com.hunter.web.model.IncomeType;
import com.hunter.web.repo.IncomeRepo;
import com.hunter.web.specification.IncomeSearchSpecification;
import com.hunter.web.util.SearchSpecificationBuilder;

@Service
public class IncomeService {

	@Autowired private IncomeRepo incomeRepo;

	public Income saveIncomeToDB(Income income) {
		return incomeRepo.save(income);
	}
	
	public Income findIncomeById(Long id) {
		return incomeRepo.findById(id).get();
	}

	public Page<Income> getAllIncomes(Integer pageNo, Integer pageSize) {
		return incomeRepo.findAll(PageRequest.of(pageNo, pageSize));
	}
	
	public Page<Income> getAllIncomesForType(Long incomeTypeId, Integer pageNo, Integer pageSize) {
		return incomeRepo.findAllForType(PageRequest.of(pageNo, pageSize), incomeTypeId);
	}

	public void deleteIncomeById(Long id) {
		incomeRepo.deleteById(id);
	}

	public Page<Income> searchIncomeByDateAndKeyword(String keyword, 
			String fromDate, String toDate, int pageNo, Integer pageSize) throws ParseException {

		PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
		IncomeSearchSpecification spec = (IncomeSearchSpecification) SearchSpecificationBuilder.build(fromDate, toDate, keyword, Income.class);
		return incomeRepo.findAll(spec, pageRequest);

	}
	
	public List<Income> findAllNotSyncedData() {
		return incomeRepo.findAllNotSyncedData();
	}

	public Income saveRemoteData(Income income) {
		income.setSynced(true);
		
		Long tempId = income.getId();
		if(income.getRemoteId() != null) income.setId(income.getRemoteId());
		else income.setId(0L);
		income.setRemoteId(tempId);
		
		IncomeType incomeType = income.getIncomeType();
		if(incomeType != null) {
			tempId = incomeType.getId();
			if(incomeType.getRemoteId() != null) incomeType.setId(incomeType.getRemoteId());
			else incomeType.setId(0L);
			incomeType.setRemoteId(tempId);
		}
		
		System.out.println("Saving Income with id: " + income.getId() + " remote id: " + income.getRemoteId());
		return incomeRepo.saveAndFlush(income);
	}

	public void markAsSynced(Long id, Long serverId) {
		incomeRepo.markAsSynced(id, serverId);
	}

}
