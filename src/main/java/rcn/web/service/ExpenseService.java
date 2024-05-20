package rcn.web.service;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import rcn.web.model.Expense;
import rcn.web.repo.ExpenseRepo;
import rcn.web.specification.ExpenseSearchSpecification;
import rcn.web.util.SearchSpecificationBuilder;

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
		return expenseRepo.findAll(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}

	public Page<Expense> getAllExpensesForType(Long expenseTypeId, Integer pageNo, Integer pageSize) {
		return expenseRepo.findAllForType(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()), expenseTypeId);
	}
	
	public Page<Expense> getAllExpensesForTypeName(String expenseType, Integer pageNo, Integer pageSize) {
		return expenseRepo.findAllForTypeName(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()), expenseType);
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

}
