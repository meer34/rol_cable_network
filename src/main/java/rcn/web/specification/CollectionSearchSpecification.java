package rcn.web.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import rcn.web.model.Collection;
import rcn.web.util.SearchCriteria;
import rcn.web.util.SearchSpecification;

@SuppressWarnings("serial")
public class CollectionSearchSpecification extends SearchSpecification<Collection> implements Specification<Collection>{

	public CollectionSearchSpecification(List<SearchCriteria> criteriaList) {
		super(criteriaList);
	}
	
}
