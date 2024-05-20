package com.hunter.web.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.hunter.web.model.Reminder;
import com.hunter.web.util.SearchCriteria;
import com.hunter.web.util.SearchSpecification;

@SuppressWarnings("serial")
public class ReminderSearchSpecification extends SearchSpecification<Reminder> implements Specification<Reminder>{

	public ReminderSearchSpecification(List<SearchCriteria> criteriaList) {
		super(criteriaList);
	}
	
}
