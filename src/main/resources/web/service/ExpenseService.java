package com.hunter.web.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.hunter.web.model.Expense;
import com.hunter.web.model.ExpenseType;
import com.hunter.web.repo.ExpenseRepo;
import com.hunter.web.specification.ExpenseSearchSpecification;
import com.hunter.web.util.SearchSpecificationBuilder;

@Service
public class ExpenseService {

	@Autowired private ExpenseRepo expenseRepo;

	public Expense saveExpenseToDB(Expense expense) {
		return expenseRepo.save(expense);
	}
	
	public Expense findExpenseById(Long id) {
		return expenseRepo.findById(id).get();
	}

	public Page<Expense> getAllExpenses(Integer pageNo, Integer pageSize) {
		return expenseRepo.findAll(PageRequest.of(pageNo, pageSize));
	}
	
	public Page<Expense> getAllExpensesForType(Long expenseTypeId, Integer pageNo, Integer pageSize) {
		return expenseRepo.findAllForType(PageRequest.of(pageNo, pageSize), expenseTypeId);
	}

	public void deleteExpenseById(Long id) {
		expenseRepo.deleteById(id);
	}

	public Page<Expense> searchExpenseByDateAndKeyword(String keyword, 
			String fromDate, String toDate, int pageNo, Integer pageSize) throws ParseException {

		PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
		ExpenseSearchSpecification spec = (ExpenseSearchSpecification) SearchSpecificationBuilder.build(fromDate, toDate, keyword, Expense.class);
		return expenseRepo.findAll(spec, pageRequest);

	}
	
	public List<Expense> findAllNotSyncedData() {
		return expenseRepo.findAllNotSyncedData();
	}

	public Expense saveRemoteData(Expense expense) {
		expense.setSynced(true);
		
		Long tempId = expense.getId();
		if(expense.getRemoteId() != null) expense.setId(expense.getRemoteId());
		else expense.setId(0L);
		expense.setRemoteId(tempId);
		
		ExpenseType expenseType = expense.getExpenseType();
		if(expenseType != null) {
			tempId = expenseType.getId();
			if(expenseType.getRemoteId() != null) expenseType.setId(expenseType.getRemoteId());
			else expenseType.setId(0L);
			expenseType.setRemoteId(tempId);
		}
		
		System.out.println("Saving Expense with id: " + expense.getId() + " remote id: " + expense.getRemoteId());
		return expenseRepo.saveAndFlush(expense);
	}

	public void markAsSynced(Long id, Long serverId) {
		expenseRepo.markAsSynced(id, serverId);
	}

}
