package rcn.web.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import rcn.web.model.Income;
import rcn.web.util.SearchCriteria;
import rcn.web.util.SearchSpecification;

@SuppressWarnings("serial")
public class IncomeSearchSpecification extends SearchSpecification<Income> implements Specification<Income>{

	public IncomeSearchSpecification(List<SearchCriteria> criteriaList) {
		super(criteriaList);
	}
	
}
