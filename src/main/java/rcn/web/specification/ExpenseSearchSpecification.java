package rcn.web.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import rcn.web.model.Expense;
import rcn.web.util.SearchCriteria;
import rcn.web.util.SearchSpecification;

@SuppressWarnings("serial")
public class ExpenseSearchSpecification extends SearchSpecification<Expense> implements Specification<Expense>{

	public ExpenseSearchSpecification(List<SearchCriteria> criteriaList) {
		super(criteriaList);
	}
	
}
