package rcn.web.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import rcn.web.model.Bill;
import rcn.web.util.SearchCriteria;
import rcn.web.util.SearchSpecification;

@SuppressWarnings("serial")
public class BillSearchSpecification extends SearchSpecification<Bill> implements Specification<Bill>{

	public BillSearchSpecification(List<SearchCriteria> criteriaList) {
		super(criteriaList);
	}
	
}
