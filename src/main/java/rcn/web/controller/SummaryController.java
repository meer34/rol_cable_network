package rcn.web.controller;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import rcn.web.service.SummaryService;

@Controller
@PropertySource("classpath:imperial_elevator.properties")
public class SummaryController {

	@Autowired SummaryService summaryService;

	@Value("${INITIAL_PAGE_SIZE}")
	private Integer initialPageSize;

	@GetMapping({"/", "/home"})
	public String showLandingPage(Model model,
			@RequestParam(value="fromDate", required = false) String fromDate,
			@RequestParam(value="toDate", required = false) String toDate,
			@RequestParam(value="mahajanName", required = false) String mahajanName,
			@RequestParam(value="fromAccDate", required = false) String fromAccDate,
			@RequestParam(value="toAccDate", required = false) String toAccDate,
			@RequestParam(value="category", required = false) String category) throws ParseException {

		model.addAttribute("totalIncome", summaryService.getTotalIncome());
		model.addAttribute("totalExpense", summaryService.getTotalExpense());

		if(fromDate == null && toDate == null && mahajanName == null) {
			model.addAttribute("dashboardList", null); //summaryService.getAllDashBoardItems()
		} else {
			model.addAttribute("dashboardList", null); //summaryService.getAllDashBoardItemsByDateAndMahajan(fromDate, toDate, mahajanName)
		}

		if(fromAccDate == null && toAccDate == null && category == null) {
			model.addAttribute("incomeReportList", summaryService.getIncomeReport());
			model.addAttribute("expenseReportList", summaryService.getExpenseReport());

		} else {
			model.addAttribute("incomeReportList", summaryService.getIncomeReport(fromAccDate, toAccDate, category));
			model.addAttribute("expenseReportList", summaryService.getExpenseReport(fromAccDate, toAccDate, category));
		}

		return "home";
	}

	@GetMapping("/account-report")
	public String showAccountReportPage(Model model,
			@RequestParam(value="fromDate", required = false) String fromDate,
			@RequestParam(value="toDate", required = false) String toDate,
			@RequestParam(value="category", required = false) String category) throws ParseException {

		model.addAttribute("totalIncome", summaryService.getTotalIncome());
		model.addAttribute("totalExpense", summaryService.getTotalExpense());

		if(fromDate == null && toDate == null && category == null) {
			model.addAttribute("incomeReportList", summaryService.getIncomeReport());
			model.addAttribute("expenseReportList", summaryService.getExpenseReport());

		} else {
			model.addAttribute("incomeReportList", summaryService.getIncomeReport(fromDate, toDate, category));
			model.addAttribute("expenseReportList", summaryService.getExpenseReport(fromDate, toDate, category));
		}

		return "account-report";
	}

}
