package rcn.web.service;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import rcn.web.model.Bill;
import rcn.web.model.Connection;
import rcn.web.repo.BillRepo;

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
		return billRepo.findAll(Sort.by("endDate").descending());
	}

	public Page<Bill> getAll(Integer pageNo, Integer pageSize) {
		return billRepo.findAll(PageRequest.of(pageNo, pageSize, Sort.by("endDate").descending()));
	}

	public void deleteById(Long id) {
		billRepo.deleteById(id);
	}

	@Transactional
	public Bill getBillForPeriod(Connection connection, Date dateOfConnStart, Date dateOfConnExpiry) {
		 return billRepo.findByConnectionAndStartDateAndEndDate(connection, dateOfConnStart, dateOfConnExpiry);
		
	}
	
}
