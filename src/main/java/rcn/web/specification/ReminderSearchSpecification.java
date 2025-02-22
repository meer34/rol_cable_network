package rcn.web.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import rcn.web.model.Reminder;
import rcn.web.util.SearchCriteria;
import rcn.web.util.SearchSpecification;

@SuppressWarnings("serial")
public class ReminderSearchSpecification extends SearchSpecification<Reminder> implements Specification<Reminder>{

	public ReminderSearchSpecification(List<SearchCriteria> criteriaList) {
		super(criteriaList);
	}
	
}
