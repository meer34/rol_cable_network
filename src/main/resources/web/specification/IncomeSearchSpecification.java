package com.hunter.web.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.hunter.web.model.Income;
import com.hunter.web.util.SearchCriteria;
import com.hunter.web.util.SearchSpecification;

@SuppressWarnings("serial")
public class IncomeSearchSpecification extends SearchSpecification<Income> implements Specification<Income>{

	public IncomeSearchSpecification(List<SearchCriteria> criteriaList) {
		super(criteriaList);
	}
	
}
