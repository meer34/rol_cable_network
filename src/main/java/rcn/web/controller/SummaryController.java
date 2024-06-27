package rcn.web.controller;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import rcn.web.model.Settlement;
import rcn.web.service.AppUserService;
import rcn.web.service.SummaryService;

@Controller
@PropertySource("classpath:rol_cable_network.properties")
public class SummaryController {

	@Autowired SummaryService summaryService;
	@Autowired AppUserService appUserService;

	@Value("${INITIAL_PAGE_SIZE}")
	private Integer initialPageSize;

	@GetMapping({"/", "/home"})
	public String showLandingPage(Model model) throws ParseException {

		System.out.println("Settlement user page");

		model.addAttribute("appUserList", 
				appUserService.getAllAppUsers().stream()
				.filter(appUser -> "APP_USER".equals(appUser.getUserType()))
				.collect(Collectors.toList()));

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
