package com.hunter.web.util;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class SearchSpecification<T extends Object> {
	
	private List<SearchCriteria> criteriaList;

    public SearchSpecification(List<SearchCriteria> criteriaList) {
		this.criteriaList = criteriaList;
	}
    
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		Predicate finalPredicate = null;
		Predicate tempPredicate = null;
		
		for (SearchCriteria criteria : criteriaList) {
			
			if (criteria.getOperation().equalsIgnoreCase(">")) {
				if (root.get(criteria.getKey().toString()).getJavaType() == Date.class) {
					tempPredicate = builder.greaterThanOrEqualTo(root.<Date> get(criteria.getKey()), (java.util.Date)criteria.getValue());
				} else {
					tempPredicate = builder.greaterThanOrEqualTo(root.<String> get(criteria.getKey().toString()), criteria.getValue().toString());
				}
	        }
	        else if (criteria.getOperation().equalsIgnoreCase("<")) {
	        	if (root.get(criteria.getKey().toString()).getJavaType() == Date.class) {
					tempPredicate = builder.lessThanOrEqualTo(root.<Date> get(criteria.getKey()), (java.util.Date)criteria.getValue());
				} else {
					tempPredicate = builder.lessThanOrEqualTo(root.<String> get(criteria.getKey().toString()), criteria.getValue().toString());
				}
	        } 
	        else if (criteria.getOperation().equalsIgnoreCase(":")) {
	            if (root.get(criteria.getKey().toString()).getJavaType() == String.class) {
	            	tempPredicate = builder.like(root.<String>get(criteria.getKey().toString()), "%" + criteria.getValue() + "%");
	            }
	            else {
	            	tempPredicate = builder.equal(root.get(criteria.getKey()), criteria.getValue());
	            }
	        }
			if(finalPredicate == null) finalPredicate = tempPredicate;
			else if(criteria.isOr()) finalPredicate = builder.or(finalPredicate, tempPredicate);
			else finalPredicate = builder.and(finalPredicate, tempPredicate);
			
		}
        
        return finalPredicate;
    }

}
