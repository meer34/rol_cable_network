package com.hunter.web.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hunter.web.model.AccountReport;
import com.hunter.web.model.BillRecord;
import com.hunter.web.model.DashBoard;
import com.hunter.web.model.StockOut;
import com.hunter.web.model.TotalSale;
import com.hunter.web.model.TotalStock;
import com.hunter.web.repo.SummaryRepo;

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
		
		System.out.println("From Date is - " + new SimpleDateFormat("EEE, d MMM yyyy G HH:mm:ss Z").format(fromDateConverted));
		System.out.println("To Date is - " + new SimpleDateFormat("EEE, d MMM yyyy G HH:mm:ss Z").format(toDateConverted));
		
		return summaryRepo.findAllDashBoardItemsByDateAndMahajan(fromDateConverted, toDateConverted, mahajanName);
		
	}
	
	public List<TotalStock> getTotalStocksByProductDetails() {
		return TotalStock.getListOfTotalStock(summaryRepo.findAllTotalStocksByProductDetails());
	}
	
	public List<TotalSale> getAllTotalSales() {
		return summaryRepo.findTotalSales();
	}
	
	public List<StockOut> getStockOutsByProductDetails(String productName, String size, String colour, String brand) {
		return summaryRepo.findStockOutsByProductDetails(productName, size, colour, brand);
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
	
	public List<BillRecord> getBillRecord() {
		return summaryRepo.findBillRecord();
	}

}
