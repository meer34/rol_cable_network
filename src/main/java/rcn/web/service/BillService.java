package rcn.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import rcn.web.model.Bill;
import rcn.web.repo.BillRepo;
import rcn.web.specification.BillSearchSpecification;
import rcn.web.util.SearchSpecificationBuilder;

@Service
public class BillService {

	@Autowired private BillRepo billRepo;

	public Bill save(Bill bill) {
		return billRepo.save(bill);
	}
	
	public Bill getById(Long id) {
		return billRepo.findById(id).orElse(new Bill());
	}
	
	public List<Bill> getAll() {
		return billRepo.findAll();
	}

	public Page<Bill> getAll(Integer pageNo, Integer pageSize) {
		return billRepo.findAll(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}

	public void deleteById(Long id) {
		billRepo.deleteById(id);
	}

	public Page<Bill> getPageByConnectionId(Long connectionId, Integer pageNo, Integer pageSize) {
		return billRepo.findPageByConnection(connectionId, PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}
	
	public Page<Bill> getPageByDateAndConsumer(String fromDate, String toDate, 
			Long consumerId, Integer pageNo, Integer pageSize) {
		BillSearchSpecification spec = (BillSearchSpecification) SearchSpecificationBuilder.build(fromDate, toDate, String.valueOf(consumerId), Bill.class);
		return billRepo.findAll(spec, PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}
	
}
