package com.hunter.web.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.hunter.web.model.StockOut;
import com.hunter.web.util.SearchCriteria;
import com.hunter.web.util.SearchSpecification;

@SuppressWarnings("serial")
public class StockOutSearchSpecification extends SearchSpecification<StockOut> implements Specification<StockOut>{

	public StockOutSearchSpecification(List<SearchCriteria> criteriaList) {
		super(criteriaList);
	}
	
}
