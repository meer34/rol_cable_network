package rcn.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import rcn.web.model.Connection;
import rcn.web.repo.ConnectionRepo;
import rcn.web.specification.ConnectionSearchSpecification;
import rcn.web.util.SearchSpecificationBuilder;

@Service
public class ConnectionService {

	@Autowired private ConnectionRepo connectionRepo;

	public Connection save(Connection admin) {
		return connectionRepo.save(admin);
	}
	
	public Connection getById(Long id) {
		return connectionRepo.findById(id).orElse(null);
	}
	
	public Connection getByConsumerId(Long consumerId) {
		return connectionRepo.findByConsumerId(consumerId);
	}
	
	public Page<Connection> getPageByConsumerId(Long consumerId, Integer pageNo, Integer pageSize) {
		return connectionRepo.findPageByConsumerId(consumerId, PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}

	public Page<Connection> getAll(Integer pageNo, Integer pageSize) {
		return connectionRepo.findAll(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}

	public void deleteById(Long id) {
		connectionRepo.deleteById(id);
	}

	public Page<Connection> searchByDateAndKeyword(String keyword, 
			String fromDate, String toDate, int pageNo, Integer pageSize) {

		PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
		ConnectionSearchSpecification spec = (ConnectionSearchSpecification) SearchSpecificationBuilder.build(fromDate, toDate, keyword, Connection.class);
		return connectionRepo.findAll(spec, pageRequest);

	}

}
