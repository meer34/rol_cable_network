package rcn.web.service;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import rcn.web.model.Income;
import rcn.web.repo.IncomeRepo;
import rcn.web.specification.IncomeSearchSpecification;
import rcn.web.util.SearchSpecificationBuilder;

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
		return incomeRepo.findAll(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}
	
	public Page<Income> getAllIncomesForTypeId(Long incomeTypeId, Integer pageNo, Integer pageSize) {
		return incomeRepo.findAllForType(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()), incomeTypeId);
	}
	
	public Page<Income> getAllIncomesForTypeName(String incomeType, Integer pageNo, Integer pageSize) {
		return incomeRepo.findAllForTypeName(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()), incomeType);
	}
	
	public Page<Income> getAllIncomesForAppUser(Long appUserId, Integer pageNo, Integer pageSize) {
		return incomeRepo.findAllForAppUser(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()), appUserId);
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
	
}
