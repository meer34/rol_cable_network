package rcn.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import rcn.web.model.Collection;
import rcn.web.repo.CollectionRepo;

@Service
public class CollectionService {

	@Autowired private CollectionRepo collectionRepo;

	public Collection save(Collection collection) {
		return collectionRepo.save(collection);
	}
	
	public Collection getById(Long id) {
		return collectionRepo.findById(id).orElse(null);
	}
	
	public List<Collection> getAll() {
		return collectionRepo.findAll();
	}

	public Page<Collection> getAll(Integer pageNo, Integer pageSize) {
		return collectionRepo.findAll(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}

	public void deleteById(Long id) {
		collectionRepo.deleteById(id);
	}

	public List<Collection> getAllByConsumer(Long consumerId, Integer pageNo, Integer pageSize) {
		return collectionRepo.findAllForConsumer(consumerId);
	}
	
	public Page<Collection> getPageByConsumer(Long consumerId, Integer pageNo, Integer pageSize) {
		return collectionRepo.findAllForConsumer(consumerId, PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}
	
}
