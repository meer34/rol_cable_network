package rcn.web.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import rcn.web.model.Connection;
import rcn.web.util.SearchCriteria;
import rcn.web.util.SearchSpecification;

@SuppressWarnings("serial")
public class ConnectionSearchSpecification extends SearchSpecification<Connection> implements Specification<Connection>{

	public ConnectionSearchSpecification(List<SearchCriteria> criteriaList) {
		super(criteriaList);
	}
	
}
