package rcn.web.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import rcn.web.model.AccountReport;
import rcn.web.model.BillRecord;
import rcn.web.model.DashBoard;
import rcn.web.model.StockOut;
import rcn.web.model.TotalSale;
import rcn.web.model.TotalStock;
import rcn.web.repo.SummaryRepo;

@Service
public class SummaryService {

	@Autowired private SummaryRepo summaryRepo;

	public List<DashBoard> getAllDashBoardItems() {
		return summaryRepo.findAllDashBoardItems();
	}
	
	public List<DashBoard> getAllDashBoardItemsByDateAndMahajan(String fromDate, String toDate, String mahajanName) throws ParseException {
		Date fromDateConverted = null;
		Date toDateConverted = null;
		
		if(fromDate == null || "".equals(fromDate)) fromDateConverted = new Date(-50000000000000L);
		else fromDateConverted = new SimpleDateFormat("yyyy-MM-dd").parse(fromDate);
		
		if(toDate == null || "".equals(toDate)) toDateConverted = new Date(50000000000000L);
		else toDateConverted = new SimpleDateFormat("yyyy-MM-dd").parse(toDate);
		
		//System.out.println("From Date is - " + new SimpleDateFormat("EEE, d MMM yyyy G HH:mm:ss Z").format(fromDateConverted));
		//System.out.println("To Date is - " + new SimpleDateFormat("EEE, d MMM yyyy G HH:mm:ss Z").format(toDateConverted));
		
		return summaryRepo.findAllDashBoardItemsByDateAndMahajan(fromDateConverted, toDateConverted, mahajanName);
		
	}

	public Page<TotalStock> getTotalStocksBySortNo(int pageNo, int pageSize) {
		return summaryRepo.findAllTotalStocksBySortNo(PageRequest.of(pageNo, pageSize));
	}
	
	public Page<TotalStock> getTotalStocksByMahajanName(int pageNo, int pageSize) {
		return summaryRepo.findAllTotalStocksByMahajanName(PageRequest.of(pageNo, pageSize));
	}
	
	public List<TotalSale> getAllTotalSales() {
		return summaryRepo.findAllByTotalSales();
	}
	
	public List<TotalSale> getTotalSalesBySortNoOrRollNo(String keyword) {
		return summaryRepo.findTotalSalesBySortNoOrRollNo(keyword);
	}
	
	public List<StockOut> getStockOutsBySortNoAndRollNo(String sortNo, String rollNo) {
		return summaryRepo.findStockOutsBySortNoAndRollNo(sortNo, rollNo);
	}
	
	public Long getTotalIncome() {
		return summaryRepo.findTotalIncome();
	}
	
	public Long getTotalExpense() {
		return summaryRepo.findTotalExpense();
	}
	
	public List<AccountReport> getIncomeReport() {
		return summaryRepo.findIncomeReport();
	}
	
	public List<AccountReport> getExpenseReport() {
		return summaryRepo.findExpenseReport();
	}
	
	public List<BillRecord> getBillRecord() {
		return summaryRepo.findBillRecord();
	}

}
