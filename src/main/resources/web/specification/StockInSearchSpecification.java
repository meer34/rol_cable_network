package com.hunter.web.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.hunter.web.model.StockIn;
import com.hunter.web.util.SearchCriteria;
import com.hunter.web.util.SearchSpecification;

@SuppressWarnings("serial")
public class StockInSearchSpecification extends SearchSpecification<StockIn> implements Specification<StockIn>{

	public StockInSearchSpecification(List<SearchCriteria> criteriaList) {
		super(criteriaList);
	}
	
}
