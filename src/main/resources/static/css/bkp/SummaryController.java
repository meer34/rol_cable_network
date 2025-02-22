package rcn.web.controller;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import rcn.web.model.TotalStock;
import rcn.web.service.SummaryService;

@Controller
public class SummaryController {

	@Autowired SummaryService summaryService;
	
	@GetMapping({"/", "/home"})
	public String showLandingPage(Model model) {
		model.addAttribute("dashboardList", summaryService.getAllDashBoardItems());
		return "home";
	}
	
	@RequestMapping(value = "/searchStockDashboard",
			method = RequestMethod.GET)
	public String searchStockDashboard(Model model,
			@RequestParam(value="fromDate", required = false) String fromDate,
			@RequestParam(value="toDate", required = false) String toDate,
			@RequestParam(value="mahajanName", required = false) String mahajanName) throws Exception{
		
		model.addAttribute("dashboardList", summaryService.getAllDashBoardItemsByDateAndMahajan(fromDate, toDate, mahajanName));
		return "home";

	}

	@GetMapping("/total-stock")
	public String showTotalStockPage(Model model,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			@RequestParam(value="loadDataBy", required = false) String loadDataBy) {
		
		System.out.println("Inside showTotalStockPage");
		Page<TotalStock> listPage = null;
		if(loadDataBy == null || "sortNo".equalsIgnoreCase(loadDataBy)) {
			listPage = summaryService.getTotalStocksBySortNo(page.orElse(1) - 1, size.orElse(4));
			model.addAttribute("loadedBy", "sortNo");
			System.out.println("111111111111111");
			
		} else if("mahajanName".equalsIgnoreCase(loadDataBy)) {
			listPage = summaryService.getTotalStocksByMahajanName(page.orElse(1) - 1, size.orElse(4));
			model.addAttribute("loadedBy", "mahajanName");
			System.out.println("222222222222222222");
		}
		
		model.addAttribute("listPage", listPage);
		
		int totalPages = listPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
					.boxed()
					.collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}
		
		return "total-stock";
	}
	
	@GetMapping("/total-sell")
	public String showTotalSale(Model model) {
		model.addAttribute("totalSaleList", summaryService.getAllTotalSales());
		return "total-sell";
	}
	
	@RequestMapping(value = "/searchTotalSale",
			method = RequestMethod.GET)
	public String searchTotalSale(Model model, @RequestParam(value="keyword") String keyword) throws Exception{
		
		model.addAttribute("totalSaleList", summaryService.getTotalSalesBySortNoOrRollNo(keyword)); //TODO
		return "total-sell";

	}
	
	@RequestMapping(value = "/viewStockOutsBySortNoAndRollNo",
			method = RequestMethod.GET)
	public String viewStockOutsBySortNoAndRollNo(Model model, 
			@RequestParam("sortNo") String sortNo,
			@RequestParam("rollNo") String rollNo) throws Exception{

		model.addAttribute("stockOutList", summaryService.getStockOutsBySortNoAndRollNo(sortNo, rollNo));
		return "stock-out";

	}
	
	@GetMapping("/account-report")
	public String showAccountReportPage(Model model) {
		model.addAttribute("totalIncome", summaryService.getTotalIncome());
		model.addAttribute("totalExpense", summaryService.getTotalExpense());
		model.addAttribute("incomeReportList", summaryService.getIncomeReport());
		model.addAttribute("expenseReportList", summaryService.getExpenseReport());
		return "account-report";
	}

}
