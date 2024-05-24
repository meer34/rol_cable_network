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

import rcn.web.model.Employee;
import rcn.web.service.EmployeeService;

@Controller
@PropertySource("classpath:rol_cable_network.properties")
public class EmployeeController {
	
	@Autowired EmployeeService employeeService;
	
	@Value("${INITIAL_PAGE_SIZE}")
	private Integer initialPageSize;
	
	@GetMapping("/employee")
	public String showEmployees(Model model,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			@RequestParam(value="keyword", required = false) String keyword) throws ParseException {
		
		Page<Employee> listPage = null;
		
		if(keyword == null) {
			System.out.println("Employee home page");
			listPage = employeeService.getAllEmployees(page.orElse(1) - 1, size.orElse(initialPageSize));
			
		} else {
			System.out.println("Searching Employee for name/designation: " + keyword);
			listPage = employeeService.searchEmployeeByNameAndDesignation(keyword, page.orElse(1) - 1, size.orElse(initialPageSize));
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
		
		return "employee";

	}

	@GetMapping("/addEmployeePage")
	public String showAddEmployeePage(Model model) {
		model.addAttribute("header", "Add Employee");
		return "employee-create";
		
	}
	
	@RequestMapping(value = "/editEmployee",
			method = RequestMethod.GET)
	public String editExpensePage(Model model, @RequestParam("id") Long id) throws Exception{

		System.out.println("Got edit request for Employee with id " + id);
		model.addAttribute("header", "Edit Employee");
		model.addAttribute("employee", employeeService.findEmployeeById(id));
		
		return "employee-create";

	}
	
	@RequestMapping(value = "/saveEmployee",
			method = RequestMethod.POST)
	public String saveEmployee(Model model, Employee employee, RedirectAttributes redirectAttributes) throws Exception{
		employeeService.saveEmployeeToDB(employee);
		redirectAttributes.addFlashAttribute("successMessage", "Employee record saved successfully!");
		return "redirect:/employee";

	}
	
	@RequestMapping(value = "/viewEmployee",
			method = RequestMethod.GET)
	public String viewEmployee(Model model, @RequestParam("id") Long id) throws Exception{

		System.out.println("Got view request for Employee with id: " + id);
		model.addAttribute("employee", employeeService.findEmployeeById(id));
		return "view-employee";

	}

	@RequestMapping(value = "/deleteEmployee",
			method = RequestMethod.GET)
	public String deleteEmployee(RedirectAttributes redirectAttributes, @RequestParam("id") Long id) throws IOException {

		System.out.println("Got delete request for Employee with id: " + id);
		employeeService.deleteEmployeeById(id);
		redirectAttributes.addFlashAttribute("successMessage", "Employee record deleted successfully!");
		
		return "redirect:/employee";

	}
	
}
