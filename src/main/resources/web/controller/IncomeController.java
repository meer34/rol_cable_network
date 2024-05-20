package com.hunter.web.controller;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hunter.web.model.Income;
import com.hunter.web.model.IncomeType;
import com.hunter.web.repo.IncomeTypeRepo;
import com.hunter.web.service.IncomeService;
import com.hunter.web.service.ModeratorService;

@Controller
@PropertySource("classpath:hunter_garments.properties")
public class IncomeController {

	@Autowired IncomeService incomeService;
	@Autowired IncomeTypeRepo incomeTypeRepo;
	@Autowired ModeratorService moderatorService;

	@Value("${INITIAL_PAGE_SIZE}")
	private Integer initialPageSize;

	static String incomeBillPath = "/files/hunter_garments/bill/income/";
	static String downloadBasePath = "/files/hunter_garments/";

	@GetMapping("/income-type")
	public String showIncomeTypes(Model model) {
		model.addAttribute("incomeTypeList", incomeTypeRepo.findAll());
		return "income-category";

	}

	@GetMapping("/addIncomeTypePage")
	public String showAddIncomeTypePage(Model model) {

		model.addAttribute("header", "Add Income Category");
		return "add-income-category";

	}

	@RequestMapping(value = "/editIncomeTypePage",
			method = RequestMethod.GET)
	public String editIncomeTypePage(Model model, @RequestParam("id") Long id) throws Exception{

		System.out.println("Got edit request for income type with id " + id);

		model.addAttribute("header", "Edit Income Category");
		model.addAttribute("incomeType", incomeTypeRepo.findById(id).get());

		return "add-income-category";

	}

	@RequestMapping(value = "/saveIncomeType",
			method = RequestMethod.POST)
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
			@RequestParam(value="incomeTypeId", required = false) Long incomeTypeId) throws ParseException {

		Page<Income> listPage = null;

		if(keyword == null && fromDate == null && toDate == null) {
			System.out.println("Income home page");
			if(incomeTypeId != null) listPage = incomeService.getAllIncomesForType(incomeTypeId, page.orElse(1) - 1, size.orElse(initialPageSize));
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
	public String showAddIncomePage(Model model) {

		model.addAttribute("users", moderatorService.getAllUsers());
		model.addAttribute("incomeTypes", incomeTypeRepo.findAll());
		model.addAttribute("header", "Add Income");

		return "income-create";

	}

	@RequestMapping(value = "/editIncomePage",
			method = RequestMethod.GET)
	public String editIncomePage(Model model, @RequestParam("id") Long id) throws Exception{

		System.out.println("Got edit request for income with id " + id);

		model.addAttribute("users", moderatorService.getAllUsers());
		model.addAttribute("incomeTypes", incomeTypeRepo.findAll());
		model.addAttribute("header", "Edit Income");

		model.addAttribute("income", incomeService.findIncomeById(id));

		return "income-create";

	}

	@RequestMapping(value = "/saveIncome",
			method = RequestMethod.POST)
	public String saveIncome(Model model, Income income, RedirectAttributes redirectAttributes) throws Exception{

		try {
			String fileName = StringUtils.cleanPath(income.getBillFile().getOriginalFilename());
			if(!"".equals(fileName)) {
				String incomeBillFileName = "Bill_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_" + fileName;
				Files.createDirectories(Paths.get(incomeBillPath));
				Path path = Paths.get(incomeBillPath + incomeBillFileName);
				Files.copy(income.getBillFile().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				if(income.getBillFileName() != null && !"".equals(income.getBillFileName())) {
					Files.delete(Paths.get(incomeBillPath + income.getBillFileName()));
				}

				income.setBillFileName(incomeBillFileName);
			}

		} catch (IOException e) {
			System.out.println("Exception while saving income bill file in local - " + e.getMessage());
		}

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
	public String deleteIncome(RedirectAttributes redirectAttributes, @RequestParam("id") Long id) throws IOException {

		System.out.println("Got delete request for income id " + id);
		String billFileName = incomeService.findIncomeById(id).getBillFileName();
		try {
			if(billFileName != null && !billFileName.equals("")) Files.delete(Paths.get(incomeBillPath + billFileName));
		} catch(Exception e) {
			System.out.println("Bill file not found with name - " + billFileName);
		}
		
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
