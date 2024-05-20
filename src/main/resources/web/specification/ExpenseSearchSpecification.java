package com.hunter.web.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.hunter.web.model.Expense;
import com.hunter.web.util.SearchCriteria;
import com.hunter.web.util.SearchSpecification;

@SuppressWarnings("serial")
public class ExpenseSearchSpecification extends SearchSpecification<Expense> implements Specification<Expense>{

	public ExpenseSearchSpecification(List<SearchCriteria> criteriaList) {
		super(criteriaList);
	}
	
}
