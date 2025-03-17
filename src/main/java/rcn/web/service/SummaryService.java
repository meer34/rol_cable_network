package rcn.web.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rcn.web.model.AccountReport;
import rcn.web.repo.SummaryRepo;

@Service
public class SummaryService {

	@Autowired private SummaryRepo summaryRepo;
	
	public Long getTotalCollection() {
		return summaryRepo.findTotalCollection();
	}
	
	public Long getTotalIncome() {
		return summaryRepo.findTotalIncome();
	}
	
	public Long getTotalExpense() {
		return summaryRepo.findTotalExpense();
	}
	
	public List<AccountReport> getCollectionReport() {
		return summaryRepo.findCollectionReport();
	}
	
	public List<AccountReport> getCollectionReport(String fromDate, String toDate, String category) throws ParseException {
		Date fromDateConverted = null;
		Date toDateConverted = null;
		
		if(fromDate == null || "".equals(fromDate)) fromDateConverted = new Date(-50000000000000L);
		else fromDateConverted = new SimpleDateFormat("yyyy-MM-dd").parse(fromDate);
		
		if(toDate == null || "".equals(toDate)) toDateConverted = new Date(50000000000000L);
		else toDateConverted = new SimpleDateFormat("yyyy-MM-dd").parse(toDate);
		return summaryRepo.findCollectionReport(fromDateConverted, toDateConverted, category);
	}
	
	public List<AccountReport> getIncomeReport() {
		return summaryRepo.findIncomeReport();
	}
	
	public List<AccountReport> getIncomeReport(String fromDate, String toDate, String category) throws ParseException {
		Date fromDateConverted = null;
		Date toDateConverted = null;
		
		if(fromDate == null || "".equals(fromDate)) fromDateConverted = new Date(-50000000000000L);
		else fromDateConverted = new SimpleDateFormat("yyyy-MM-dd").parse(fromDate);
		
		if(toDate == null || "".equals(toDate)) toDateConverted = new Date(50000000000000L);
		else toDateConverted = new SimpleDateFormat("yyyy-MM-dd").parse(toDate);
		
		return summaryRepo.findIncomeReport(fromDateConverted, toDateConverted, category);
	}
	
	public List<AccountReport> getExpenseReport() {
		return summaryRepo.findExpenseReport();
	}
	
	public List<AccountReport> getExpenseReport(String fromDate, String toDate, String category) throws ParseException {
		Date fromDateConverted = null;
		Date toDateConverted = null;
		
		if(fromDate == null || "".equals(fromDate)) fromDateConverted = new Date(-50000000000000L);
		else fromDateConverted = new SimpleDateFormat("yyyy-MM-dd").parse(fromDate);
		
		if(toDate == null || "".equals(toDate)) toDateConverted = new Date(50000000000000L);
		else toDateConverted = new SimpleDateFormat("yyyy-MM-dd").parse(toDate);
		
		return summaryRepo.findExpenseReport(fromDateConverted, toDateConverted, category);
	}

}
