package rcn.web.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import rcn.web.model.Settlement;
import rcn.web.util.SearchCriteria;
import rcn.web.util.SearchSpecification;

@SuppressWarnings("serial")
public class SettlementSearchSpecification extends SearchSpecification<Settlement> implements Specification<Settlement>{

	public SettlementSearchSpecification(List<SearchCriteria> criteriaList) {
		super(criteriaList);
	}
	
}
