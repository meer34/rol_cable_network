package com.hunter.web.controller;

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

import com.hunter.web.model.StockOut;
import com.hunter.web.service.CustomerService;
import com.hunter.web.service.StockInService;
import com.hunter.web.service.StockOutService;

@Controller
@PropertySource("classpath:hunter_garments.properties")
public class StockOutController {

	@Autowired StockOutService stockOutService;
	@Autowired StockInService stockInService;
	@Autowired CustomerService customerService;
	@Value("${INITIAL_PAGE_SIZE}") private Integer initialPageSize;

	@GetMapping("/stock-out")
	public String showStockIn(Model model,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			@RequestParam(value="fromDate", required = false) String fromDate,
			@RequestParam(value="toDate", required = false) String toDate,
			@RequestParam(value="keyword", required = false) String keyword,
			@RequestParam(value="name", required = false) String name,
			@RequestParam(value="pSize", required = false) String pSize,
			@RequestParam(value="colour", required = false) String colour,
			@RequestParam(value="brand", required = false) String brand) throws ParseException {

		Page<StockOut> listPage = null;

		if(keyword == null && fromDate == null && toDate == null) {
			System.out.println("StockOut home page");
			if(name != null) listPage = stockOutService.getAllStockOutsForProduct(name, pSize, colour, brand, page.orElse(1) - 1, size.orElse(initialPageSize));
			else listPage = stockOutService.getAllStockOuts(page.orElse(1) - 1, size.orElse(initialPageSize));

		} else {
			System.out.println("Searching StockOut for fromDate:" + fromDate + " and toDate:" +toDate +" and keyword:" + keyword);
			listPage = stockOutService.searchStockOutByDateAndKeyword(keyword, fromDate, toDate, page.orElse(1) - 1, size.orElse(initialPageSize));

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

		return "stock-out";

	}

	@GetMapping("/addStockOutPage")
	public String showAddStockOutPage(Model model) {

		model.addAttribute("customers", customerService.getAllUsers());
		model.addAttribute("productNames", stockInService.getAllStockInProductNames());
		model.addAttribute("header", "Add Stock Out");

		return "stock-out-create";

	}

	@RequestMapping(value = "/editStockOutPage",
			method = RequestMethod.GET)
	public String editStockOutPage(Model model, @RequestParam("id") String id) throws Exception{

		System.out.println("Got edit request for stock-out id " + id);

		model.addAttribute("customers", customerService.getAllUsers());
		model.addAttribute("productNames", stockInService.getAllStockInProductNames());
		model.addAttribute("header", "Edit Stock Out");

		model.addAttribute("stockOut", stockOutService.findStockOutById(Long.parseLong(id)));

		return "stock-out-create";

	}

	@RequestMapping(value = "/saveStockOut",
			method = RequestMethod.POST)
	public String saveStockOut(Model model, StockOut stockOut, 
			RedirectAttributes redirectAttributes, @RequestParam(value="addProductFlag", required = false) String addProductFlag) throws Exception{
		if(addProductFlag == null) {
			stockOutService.saveStockOutToDB(stockOut);
			redirectAttributes.addFlashAttribute("successMessage", "Stock Out saved successfully!");
			return "redirect:/stock-out";
		} else {
			StockOut savedStockOut = stockOutService.saveStockOutToDB(stockOut);
			return "redirect:/editStockOutPage?action=Edit&id=" + savedStockOut.getId();
		}

	}

	@RequestMapping(value = "/viewStockOut",
			method = RequestMethod.GET)
	public String viewStockOut(Model model, @RequestParam("id") String id) throws Exception{

		System.out.println("Got view request for stock_out id " + id);
		model.addAttribute("stockOut", stockOutService.findStockOutById(Long.parseLong(id)));
		return "stock-out-view";

	}

	@RequestMapping(value = "/deleteStockOut",
			method = RequestMethod.GET)
	public String deleteStockOut(RedirectAttributes redirectAttributes, @RequestParam("id") String id) throws Exception{

		System.out.println("Got delete request for stock-out id " + id);
		stockOutService.deleteStockOutById(Long.parseLong(id));
		redirectAttributes.addFlashAttribute("successMessage", "Stock Out with id " + id + " deleted successfully!");
		return "redirect:/stock-out";

	}

}
