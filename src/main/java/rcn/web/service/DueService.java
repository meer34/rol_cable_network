package rcn.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import rcn.web.model.Due;
import rcn.web.repo.DueRepo;

@Service
public class DueService {

	@Autowired private DueRepo dueRepo;
	
	public Due saveDue(Due due) {
		return dueRepo.save(due);
	}
	
	public Due getDueById(Long id) {
		return dueRepo.findById(id).orElse(null);
	}
	
	public List<Due> getAllDues() {
		return dueRepo.findAll();
	}
	
	public Page<Due> getAllDues(Integer pageNo, Integer pageSize) {
		return dueRepo.findAll(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}

	public void deleteDueById(Long id) {
		dueRepo.deleteById(id);
	}

	/*public Page<Due> searchDueByKeyword(String keyword, int pageNo, Integer pageSize) {
		PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
		return dueRepo.findAll(Specification.where(EntitySpecification.textInAllStringColumns(keyword)), pageRequest);
	}*/

}
