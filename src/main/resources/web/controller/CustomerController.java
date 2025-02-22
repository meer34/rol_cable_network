package com.hunter.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hunter.web.model.Customer;
import com.hunter.web.service.CustomerService;

@Controller
public class CustomerController {

	@Autowired private CustomerService customerService;

	@GetMapping("/customer")
	public String showPartyPage(Model model) {
		model.addAttribute("customerList", customerService.getAllUsers());
		return "customer";
	}

	@GetMapping("/add-customer-page")
	public String showAddPartyPage(Model model) {
		model.addAttribute("customer", new Customer());
		model.addAttribute("header", "Create Customer");
		return "customer-create";
	}

	@RequestMapping(value = "/createCustomer",
			method = RequestMethod.POST)
	public String createCustomer(Model model, Customer customer, 
			RedirectAttributes redirectAttributes) throws Exception{

		customer = customerService.saveUserToDB(customer);

		redirectAttributes.addFlashAttribute("successMessage", "New customer " + customer.getName() + " added successfully as Admin!");
		return "redirect:/customer";

	}

	@RequestMapping(value = "/viewCustomer",
			method = RequestMethod.GET)
	public String viewCustomer(Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got view request for customer id " + id);
		model.addAttribute("customer", customerService.findUserById(Long.parseLong(id)));
		return "view-customer";

	}

	@RequestMapping(value = "/editCustomer",
			method = RequestMethod.GET)
	public String editCustomer(Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got edit request for customer id " + id);
		model.addAttribute("customer", customerService.findUserById(Long.parseLong(id)));
		model.addAttribute("header", "Edit Customer");
		return "customer-create";

	}

	@RequestMapping(value = "/deleteCustomer",
			method = RequestMethod.GET)
	public String deleteCustomer(RedirectAttributes redirectAttributes,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got delete request for customer id " + id);
		Customer customer = customerService.findUserById(Long.parseLong(id));
		
		if(customer != null) {
			if(customer.getStockOutList().isEmpty()) {
				customerService.deleteUserById(Long.parseLong(id));
				redirectAttributes.addFlashAttribute("successMessage", "Customer with id " + id + " deleted successfully!");
			} else {
				redirectAttributes.addFlashAttribute("successMessage", customer.getStockOutList().size() + " Stock Out entries present this customer");
			}
		}
		return "redirect:/customer";
	}

}
