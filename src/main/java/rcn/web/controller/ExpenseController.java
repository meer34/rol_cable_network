package rcn.web.controller;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import rcn.web.model.Expense;
import rcn.web.model.ExpenseType;
import rcn.web.repo.ExpenseTypeRepo;
import rcn.web.service.AppUserService;
import rcn.web.service.ExpenseService;

@Controller
@PropertySource("classpath:rol_cable_network.properties")
public class ExpenseController {

	@Autowired ExpenseService expenseService;
	@Autowired ExpenseTypeRepo expenseTypeRepo;
	@Autowired AppUserService appUserService;

	@Value("${INITIAL_PAGE_SIZE}")
	private Integer initialPageSize;

	static String expenseBillPath = "/files/rol_cable_network/bill/expense/";

	@GetMapping("/expense-type")
	public String showExpenseTypes(Model model) {
		model.addAttribute("expenseTypeList", expenseTypeRepo.findAll());
		return "expense-category";
	}

	@GetMapping("/addExpenseTypePage")
	public String showAddExpenseTypePage(Model model) {
		model.addAttribute("header", "Add Expense Category");
		return "add-expense-category";
	}

	@RequestMapping(value = "/editExpenseTypePage",
			method = RequestMethod.GET)
	public String editExpenseTypePage(Model model, @RequestParam("id") Long id) throws Exception{

		System.out.println("Got edit request for expense type with id " + id);

		model.addAttribute("header", "Edit Expense Category");
		model.addAttribute("expenseType", expenseTypeRepo.findById(id).get());

		return "add-expense-category";

	}

	@RequestMapping(value = "/saveExpenseType",
			method = RequestMethod.POST)
	public String saveExpenseType(Model model, ExpenseType expense, RedirectAttributes redirectAttributes) throws Exception{

		expenseTypeRepo.save(expense);
		redirectAttributes.addFlashAttribute("successMessage", "Expense category saved successfully!");
		return "redirect:/expense-type";

	}

	@GetMapping("/expense")
	public String showExpenses(Model model,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			@RequestParam(value="fromDate", required = false) String fromDate,
			@RequestParam(value="toDate", required = false) String toDate,
			@RequestParam(value="keyword", required = false) String keyword,
			@RequestParam(value="expenseTypeId", required = false) Long expenseTypeId,
			@RequestParam(value="category", required = false) String expenseType) throws ParseException {

		Page<Expense> listPage = null;

		if(keyword == null && fromDate == null && toDate == null) {
			System.out.println("Expense home page");
			if(expenseTypeId != null) listPage = expenseService.getAllExpensesForType(expenseTypeId, page.orElse(1) - 1, size.orElse(initialPageSize));
			else if(expenseType != null) listPage = expenseService.getAllExpensesForTypeName(expenseType, page.orElse(1) - 1, size.orElse(initialPageSize));
			else listPage = expenseService.getAllExpenses(page.orElse(1) - 1, size.orElse(initialPageSize));

		} else {
			System.out.println("Searching Expense for fromDate:" + fromDate + " and toDate:" +toDate +" and keyword:" + keyword);
			listPage = expenseService.searchExpenseByDateAndKeyword(keyword, fromDate, toDate, page.orElse(1) - 1, size.orElse(initialPageSize));

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

		return "expense";

	}

	@GetMapping("/addExpensePage")
	public String showAddExpensePage(Model model) {

		model.addAttribute("users", appUserService.getAllUsers());
		model.addAttribute("expenseTypes", expenseTypeRepo.findAll());
		model.addAttribute("header", "Add Expense");

		return "expense-create";

	}

	@RequestMapping(value = "/editExpensePage",
			method = RequestMethod.GET)
	public String editExpensePage(Model model, @RequestParam("id") Long id) throws Exception{

		System.out.println("Got edit request for expense with id " + id);

		model.addAttribute("users", appUserService.getAllUsers());
		model.addAttribute("expenseTypes", expenseTypeRepo.findAll());
		model.addAttribute("header", "Edit Expense");

		model.addAttribute("expense", expenseService.findExpenseById(id));

		return "expense-create";

	}

	@RequestMapping(value = "/saveExpense",
			method = RequestMethod.POST)
	public String saveExpense(Model model, Expense expense, RedirectAttributes redirectAttributes) throws Exception{
		expenseService.saveExpenseToDB(expense);
		redirectAttributes.addFlashAttribute("successMessage", "Expense record saved successfully!");
		return "redirect:/expense";

	}

	@RequestMapping(value = "/viewExpense",
			method = RequestMethod.GET)
	public String viewIncome(Model model, @RequestParam("id") Long id) throws Exception{
		System.out.println("Got view request for expense id " + id);
		model.addAttribute("expense", expenseService.findExpenseById(id));
		return "view-expense";

	}

	@RequestMapping(value = "/deleteExpense",
			method = RequestMethod.GET)
	public String deleteIncome(RedirectAttributes redirectAttributes, @RequestParam("id") Long id) throws IOException {
		System.out.println("Got delete request for expense id " + id);
		expenseService.deleteExpenseById(id);
		redirectAttributes.addFlashAttribute("successMessage", "Expense record deleted successfully!");
		return "redirect:/expense";

	}

}
