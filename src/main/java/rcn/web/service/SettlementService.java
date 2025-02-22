package rcn.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import rcn.web.model.Settlement;
import rcn.web.repo.SettlementRepo;
import rcn.web.specification.SettlementSearchSpecification;
import rcn.web.util.SearchSpecificationBuilder;

@Service
public class SettlementService {

	@Autowired private SettlementRepo settlementRepo;

	public Settlement save(Settlement settlement) {
		return settlementRepo.save(settlement);
	}
	
	public Settlement getById(Long id) {
		return settlementRepo.findById(id).orElse(null);
	}
	
	public List<Settlement> getAll() {
		return settlementRepo.findAll();
	}

	public Page<Settlement> getAll(Integer pageNo, Integer pageSize) {
		return settlementRepo.findAll(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}

	public void deleteById(Long id) {
		settlementRepo.deleteById(id);
	}

	public List<Settlement> getAllByAppUser(Long appUserId, Integer pageNo, Integer pageSize) {
		return settlementRepo.findAllForAppUser(appUserId);
	}
	
	public Page<Settlement> getPageByAppUser(Long appUserId, Integer pageNo, Integer pageSize) {
		return settlementRepo.findAllForAppUser(appUserId, PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}
	
	public Page<Settlement> searchCollectionByDateAndKeyword(String keyword, 
			String fromDate, String toDate, int pageNo, Integer pageSize) {
		PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
		SettlementSearchSpecification spec = (SettlementSearchSpecification) SearchSpecificationBuilder.build(fromDate, toDate, keyword, Settlement.class);
		return settlementRepo.findAll(spec, pageRequest);
	}
	
}
