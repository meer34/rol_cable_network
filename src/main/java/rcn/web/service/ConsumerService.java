package rcn.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import rcn.web.model.Consumer;
import rcn.web.repo.ConsumerRepo;
import rcn.web.specification.EntitySpecification;

@Service
public class ConsumerService {

	@Autowired private ConsumerRepo consumerRepo;

	public Consumer save(Consumer admin) {
		return consumerRepo.save(admin);
	}
	
	public Consumer getById(Long id) {
		return consumerRepo.findById(id).get();
	}
	
	public List<Consumer> getAll() {
		return consumerRepo.findAll();
	}

	public Page<Consumer> getAll(Integer pageNo, Integer pageSize) {
		return consumerRepo.findAll(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}

	public void deleteById(Long id) {
		consumerRepo.deleteById(id);
	}

	public Page<Consumer> getAllByArea(String area, Integer pageNo, Integer pageSize) {
		return consumerRepo.findConsumerByArea(area, PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}
	
	public Page<Consumer> getAllByAreaAndKeyword(String area, String keyword, Integer pageNo, Integer pageSize) {
		Specification<Consumer> spec1 = EntitySpecification.textInAllStringColumns(keyword);
		Specification<Consumer> spec2 = EntitySpecification.filterArea(area);
		
		return consumerRepo.findAll(Specification.where(spec1).and(spec2), PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
		
	}
	
	public Page<Consumer> getAllByKeyword(String keyword, Integer pageNo, Integer pageSize) {
		return consumerRepo.findAll(Specification.where(EntitySpecification.textInAllStringColumns(keyword)), PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}

}
