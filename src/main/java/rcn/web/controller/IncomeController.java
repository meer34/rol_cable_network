package rcn.web.controller;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import rcn.web.model.Income;
import rcn.web.model.IncomeType;
import rcn.web.repo.IncomeTypeRepo;
import rcn.web.service.AppUserService;
import rcn.web.service.IncomeService;

@Controller
@PropertySource("classpath:rol_cable_network.properties")
public class IncomeController {

	@Autowired IncomeService incomeService;
	@Autowired IncomeTypeRepo incomeTypeRepo;
	@Autowired AppUserService appUserService;

	@Value("${INITIAL_PAGE_SIZE}")
	private Integer initialPageSize;

	static String incomeBillPath = "/files/rol_cable_network/bill/income/";
	static String downloadBasePath = "/files/rol_cable_network/";

	@GetMapping("/income-type")
	public String showIncomeTypes(Model model) {
		model.addAttribute("incomeTypeList", incomeTypeRepo.findAll());
		return "income-category";

	}

	@GetMapping("/addIncomeTypePage")
	@PreAuthorize("hasAuthority('ADMIN')")
	public String showAddIncomeTypePage(Model model) {

		model.addAttribute("header", "Add Income Category");
		return "add-income-category";

	}

	@RequestMapping(value = "/editIncomeTypePage",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String editIncomeTypePage(Model model, @RequestParam("id") Long id) throws Exception{

		System.out.println("Got edit request for income type with id " + id);

		model.addAttribute("header", "Edit Income Category");
		model.addAttribute("incomeType", incomeTypeRepo.findById(id).get());

		return "add-income-category";

	}

	@RequestMapping(value = "/saveIncomeType",
			method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String saveIncomeType(Model model, IncomeType income, RedirectAttributes redirectAttributes) throws Exception{

		incomeTypeRepo.save(income);
		redirectAttributes.addFlashAttribute("successMessage", "Income category saved successfully!");
		return "redirect:/income-type";

	}

	@GetMapping("/income")
	public String showIncomes(Model model,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			@RequestParam(value="fromDate", required = false) String fromDate,
			@RequestParam(value="toDate", required = false) String toDate,
			@RequestParam(value="keyword", required = false) String keyword,
			@RequestParam(value="incomeTypeId", required = false) Long incomeTypeId,
			@RequestParam(value="category", required = false) String incomeType,
			@RequestParam(value="appUserId", required = false) Long appUserId) throws ParseException {

		Page<Income> listPage = null;

		if(keyword == null && fromDate == null && toDate == null) {
			System.out.println("Income home page");
			if(incomeTypeId != null) listPage = incomeService.getAllIncomesForTypeId(incomeTypeId, page.orElse(1) - 1, size.orElse(initialPageSize));
			else if(incomeType != null) listPage = incomeService.getAllIncomesForTypeName(incomeType, page.orElse(1) - 1, size.orElse(initialPageSize));
			else if(appUserId != null) listPage = incomeService.getAllIncomesForAppUser(appUserId, page.orElse(1) - 1, size.orElse(initialPageSize));
			else listPage = incomeService.getAllIncomes(page.orElse(1) - 1, size.orElse(initialPageSize));

		} else {
			System.out.println("Searching Income for fromDate:" + fromDate + " and toDate:" +toDate +" and keyword:" + keyword);
			listPage = incomeService.searchIncomeByDateAndKeyword(keyword, fromDate, toDate, page.orElse(1) - 1, size.orElse(initialPageSize));

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

		return "income";

	}

	@GetMapping("/addIncomePage")
	@PreAuthorize("hasAnyAuthority('ADMIN','ADD_INCOME')")
	public String showAddIncomePage(Model model) {

		model.addAttribute("users", appUserService.getAllAppUsers());
		model.addAttribute("incomeTypes", incomeTypeRepo.findAll());
		model.addAttribute("header", "Add Income");

		return "income-create";

	}

	@RequestMapping(value = "/editIncomePage",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String editIncomePage(Model model, @RequestParam("id") Long id) throws Exception{

		System.out.println("Got edit request for income with id " + id);

		model.addAttribute("users", appUserService.getAllAppUsers());
		model.addAttribute("incomeTypes", incomeTypeRepo.findAll());
		model.addAttribute("header", "Edit Income");

		model.addAttribute("income", incomeService.findIncomeById(id));

		return "income-create";

	}

	@RequestMapping(value = "/saveIncome",
			method = RequestMethod.POST)
	public String saveIncome(Model model, Income income, RedirectAttributes redirectAttributes) throws Exception{

		incomeService.saveIncomeToDB(income);
		redirectAttributes.addFlashAttribute("successMessage", "Income record saved successfully!");
		return "redirect:/income";

	}

	@RequestMapping(value = "/viewIncome",
			method = RequestMethod.GET)
	public String viewIncome(Model model, @RequestParam("id") String id) throws Exception{

		System.out.println("Got view request for income id " + id);
		model.addAttribute("income", incomeService.findIncomeById(Long.parseLong(id)));
		return "view-income";

	}

	@RequestMapping(value = "/deleteIncome",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String deleteIncome(RedirectAttributes redirectAttributes, @RequestParam("id") Long id) throws IOException {
		System.out.println("Got delete request for income id " + id);
		incomeService.deleteIncomeById(id);
		redirectAttributes.addFlashAttribute("successMessage", "Income record deleted successfully!");
		return "redirect:/income";

	}

	@GetMapping("/download/{type1}/{type2}/{fileName:.+}")
	public ResponseEntity<Resource> downloadFileFromLocal(@PathVariable String type1, @PathVariable String type2, @PathVariable String fileName) {
		System.out.println("Received download request for " + type1 + " " + type2 + " - " + fileName);

		String fileBasePath = downloadBasePath + type1 + "/" + type2 + "/";
		Path path = Paths.get(fileBasePath + fileName);
		Resource resource = null;
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

}
