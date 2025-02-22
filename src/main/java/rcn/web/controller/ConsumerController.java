package rcn.web.controller;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import rcn.web.model.Area;
import rcn.web.model.Consumer;
import rcn.web.repo.AreaRepo;
import rcn.web.service.ConnectionService;
import rcn.web.service.ConsumerService;

@Controller
@RequestMapping("/consumer")
public class ConsumerController {

	@Value("${INITIAL_PAGE_SIZE}") private Integer initialPageSize;
	@Autowired ConsumerService consumerService;
	@Autowired ConnectionService connectionService;
	@Autowired AreaRepo areaRepo;

	@GetMapping
	public String showBasePage(Model model,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			@RequestParam(value="keyword", required = false) String keyword,
			@RequestParam(value="area", required = false) String area) {

		Page<Consumer> listPage = null;

		if(keyword == null && area == null) {
			System.out.println("Consumer home page");
			listPage = consumerService.getAll(page.orElse(1) - 1, size.orElse(initialPageSize));

		} else {
			System.out.println("Searching Consumer for area:" + area + " and keyword:" + keyword);
			if(keyword == null) listPage = consumerService.getAllByArea(area, page.orElse(1) - 1, size.orElse(initialPageSize));
			else if(area == null) listPage = consumerService.getAllByKeyword(keyword, page.orElse(1) - 1, size.orElse(initialPageSize));
			else listPage = consumerService.getAllByAreaAndKeyword(area, keyword, page.orElse(1) - 1, size.orElse(initialPageSize));

			model.addAttribute("area", area);
			model.addAttribute("keyword", keyword);

		}
		model.addAttribute("areaList", areaRepo.findAll());
		model.addAttribute("listPage", listPage);
		int totalPages = listPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
					.boxed()
					.collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		return "app/consumer";

	}

	@GetMapping("/add")
	@PreAuthorize("hasAnyAuthority('ADMIN','ADD_CONSUMER')")
	public String add(Model model, Consumer consumer) {
		model.addAttribute("header", "Create Consumer");
		model.addAttribute("areaList", areaRepo.findAll());
		return "app/consumer-create";
	}

	@RequestMapping(value = "/save",
			method = RequestMethod.POST)
	public String save(Model model, Consumer consumer, RedirectAttributes redirectAttributes) throws Exception{
		// Check if the STB Account Number already exists
	    if (consumer.getStbAccountNo() != null && consumerService.existsByStbAccountNo(consumer.getStbAccountNo(), consumer.getId())) {
	        redirectAttributes.addFlashAttribute("errorMessage", "STB Account No '" + consumer.getStbAccountNo() + "' already exists for another consumer!");
	        redirectAttributes.addFlashAttribute("consumer", consumer);
	        return "redirect:/consumer/add";
	    }
		consumer = consumerService.save(consumer);
		redirectAttributes.addFlashAttribute("successMessage", "Consumer " + consumer.getFullName() + " saved successfully!");
		return "redirect:/consumer";

	}

	@RequestMapping(value = "/view",
			method = RequestMethod.GET)
	public String view(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("consumerId") String consumerId) throws Exception{

		System.out.println("Got view request for consumer id " + consumerId);
		
		Consumer consumer = consumerService.getById(Long.parseLong(consumerId));
		consumer.calculateAllPendingBill();
		model.addAttribute("consumer", consumer);
		
		return "app/consumer-view";
	}

	@RequestMapping(value = "/edit",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String edit(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("consumerId") String consumerId) throws Exception{

		System.out.println("Got edit request for consumer id " + consumerId);

		model.addAttribute("consumer", consumerService.getById(Long.parseLong(consumerId)));
		model.addAttribute("areaList", areaRepo.findAll());
		model.addAttribute("header", "Edit Consumer");
		return "app/consumer-create";
	}

	@RequestMapping(value = "/delete",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String delete(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("consumerId") String consumerId) throws Exception{

		System.out.println("Got delete request for consumer id " + consumerId);

		consumerService.deleteById(Long.parseLong(consumerId));
		redirectAttributes.addFlashAttribute("successMessage", "Consumer with id " + consumerId + " deleted successfully!");
		return "redirect:/consumer";
	}

	/*
	@RequestMapping(value = "/connection",
			method = RequestMethod.GET)
	public String connection(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="consumerId", required = false) String consumerId) throws Exception{

		System.out.println("Got view request for connection id " + consumerId);
		Connection connection = connectionService.getByConsumerId(Long.parseLong(consumerId));
		if(connection != null) {
			model.addAttribute("connection", connection);
			model.addAttribute("consumerList", consumerService.getAll());
			model.addAttribute("header", "Connection for Consumer - " + connection.getConsumer().getFullName());
			return "app/connection-create";
		} else {
			redirectAttributes.addFlashAttribute("successMessage", "No connection found for Consumer with id " + consumerId);
			return "redirect:/consumer";
		}
		
	}
	*/

	@GetMapping("/area")
	public String area(Model model) {
		model.addAttribute("areaList", areaRepo.findAll());
		return "app/consumer-area";
	}

	@GetMapping("/area/add")
	@PreAuthorize("hasAuthority('ADMIN')")
	public String showAddIncomeTypePage(Model model) {

		model.addAttribute("header", "Add Consumer Area");
		return "app/consumer-area-create";

	}

	@RequestMapping(value = "/area/edit",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String editIncomeTypePage(Model model, @RequestParam("id") Long id) throws Exception{

		System.out.println("Got edit request for consumer area with id " + id);

		model.addAttribute("header", "Edit Consumer Area");
		model.addAttribute("area", areaRepo.findById(id).get());

		return "app/consumer-area-create";

	}

	@RequestMapping(value = "/area/save",
			method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String saveIncomeType(Model model, Area area, RedirectAttributes redirectAttributes) throws Exception{
		areaRepo.save(area);
		redirectAttributes.addFlashAttribute("successMessage", "Area saved successfully!");
		return "redirect:/consumer/area";

	}
	
	@GetMapping("/filter")
	public String filter(Model model,
			@RequestParam(value="filterType", required = false) String filterType,
			@RequestParam(value="filterAmount", required = false) Double filterAmount,
			@RequestParam(value="consumerId", required = false) String consumerId,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) throws ParseException {

		System.out.println("Consumer filter page. Filter Amount - " + filterAmount);
		
		
		/*
		List<Consumer> listOfConsumers = null;
		listOfConsumers = consumerService.getAll();
		
		for (Consumer consumer : listOfConsumers) {
			consumer.calculateAllPendingBill();
		}
		
		listOfConsumers = listOfConsumers.stream().filter(consumer -> consumer.getTotalPending() >= (filterAmount!=null? filterAmount: 0.0 )).collect(Collectors.toList());
		*/
		if(filterAmount == null) filterAmount = 0.0;
		Page<Consumer> listOfConsumers = consumerService.getFilteredConsumers(filterAmount, page.orElse(1) - 1, size.orElse(initialPageSize));
		System.out.println("Got it");
		model.addAttribute("listPage", listOfConsumers);
		model.addAttribute("filterType", filterType);
		model.addAttribute("filterAmount", filterAmount);
		
		int totalPages = listOfConsumers.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
					.boxed()
					.collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		return "app/consumer-filter";

	}

}
