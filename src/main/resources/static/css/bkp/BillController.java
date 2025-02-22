package rcn.web.controller;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import rcn.web.model.StockOut;
import rcn.web.service.StockInService;
import rcn.web.service.StockOutService;
import rcn.web.service.SummaryService;

@Controller
public class BillController {
	
	@Autowired StockInService stockInService;
	@Autowired StockOutService stockOutService;
	@Autowired SummaryService summaryService;

	@RequestMapping("/bill")
	public String bill(Model model,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			@RequestParam(value="fromDate", required = false) String fromDate,
			@RequestParam(value="toDate", required = false) String toDate,
			@RequestParam(value="keyword", required = false) String keyword) throws ParseException {
		
		Page<StockOut> listPage = null;
		
		if(keyword == null && fromDate == null && toDate == null) {
			System.out.println("Bill home page");
			listPage = stockOutService.getAllStockOuts(page.orElse(1) - 1, size.orElse(4));
			
		} else {
			System.out.println("Searching Bill for fromDate:" + fromDate + " and toDate:" +toDate +" and keyword:" + keyword);
			listPage = stockOutService.searchStockOutByDateAndKeyword(keyword, fromDate, toDate, page.orElse(1) - 1, size.orElse(4));
			
			model.addAttribute("fromDate", fromDate);
			model.addAttribute("toDate", toDate);
			model.addAttribute("keyword", keyword);
			
		}
		
		model.addAttribute("listPage", listPage);
		int totalPages = listPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
					.boxed()
					.collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		return "bill";

	}
	
	@RequestMapping("/bill-record")
	public String billRecord(Model model) {
		model.addAttribute("billRecordList", summaryService.getBillRecord());
		return "bill-record";
	}
	
	@RequestMapping("/billRecordsForCustomer")
	public String billsForCustomer(Model model, @RequestParam("custId") String custId) {
		model.addAttribute("stockOutList", stockOutService.getAllBillsForCustomer(Long.valueOf(custId != "" ? custId : "0")));
		return "bill";
		
	}
	
	@RequestMapping("/printStockOutBill")
	public String printGstBill(Model model, @RequestParam("printId") Long printId) {
		
		System.out.println("Received bill print request for stock id: " + printId);
		
		StockOut stockOut = stockOutService.findStockOutById(printId);
		model.addAttribute("stockOut", stockOut);
		
		if("GST".equalsIgnoreCase(stockOut.getBillType())) {
			return "bill_template_gst";
			
		} else {
			return "bill_template_non_gst";
		}
		
	}
	
}
