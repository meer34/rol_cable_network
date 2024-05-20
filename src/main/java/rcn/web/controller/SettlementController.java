package rcn.web.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import rcn.web.model.Connection;
import rcn.web.service.ConnectionService;
import rcn.web.service.ConsumerService;
import rcn.web.service.SubscriptionService;

@Controller
@RequestMapping("/settlement")
public class SettlementController {

	@Value("${INITIAL_PAGE_SIZE}") private Integer initialPageSize;
	@Autowired ConnectionService connectionService;
	@Autowired ConsumerService consumerService;
	@Autowired SubscriptionService subscriptionService;

	@GetMapping
	public String showBasePage(Model model,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			@RequestParam(value="fromDate", required = false) String fromDate,
			@RequestParam(value="toDate", required = false) String toDate,
			@RequestParam(value="keyword", required = false) String keyword,
			@RequestParam(value="consumerId", required = false) Long consumerId) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Add Connection"))) {
			return "app/my-settlement";
		}

		Page<Connection> listPage = null;

		if(keyword == null && fromDate == null && toDate == null) {
			System.out.println("Connection home page");
			if(consumerId != null) listPage = connectionService.getPageByConsumerId(consumerId, page.orElse(1) - 1, size.orElse(initialPageSize));
			else listPage = connectionService.getAll(page.orElse(1) - 1, size.orElse(initialPageSize));

		} else {
			System.out.println("Searching Connection for fromDate:" + fromDate + " and toDate:" +toDate +" and keyword:" + keyword);
			listPage = connectionService.searchByDateAndKeyword(keyword, fromDate, toDate, page.orElse(1) - 1, size.orElse(initialPageSize));

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

		return "app/connection";

	}

	@GetMapping("/add")
	public String add(Model model, Connection connection) {
		model.addAttribute("header", "Create Connection");
		model.addAttribute("consumerList", consumerService.getAll());
		model.addAttribute("packageList", subscriptionService.getAllPackages());
		model.addAttribute("bucketList", subscriptionService.getAllBuckets());
		model.addAttribute("channelList", subscriptionService.getAllChannels());
		return "app/connection-create";
	}

	@RequestMapping(value = "/save",
			method = RequestMethod.POST)
	public String save(Model model, Connection connection, RedirectAttributes redirectAttributes) throws Exception{
		connection = connectionService.save(connection);
		redirectAttributes.addFlashAttribute("successMessage", "Connection for " + connection.getConsumer().getFullName() + " saved successfully!");
		return "redirect:/connection";

	}

	@RequestMapping(value = "/view",
			method = RequestMethod.GET)
	public String view(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id,
			@RequestParam(value="consumerId", required = false) String consumerId) throws Exception{

		System.out.println("Got view request for connection id " + id);
		if(consumerId != null) {
			model.addAttribute("connection", connectionService.getByConsumerId(Long.parseLong(consumerId)));
		} else {
			model.addAttribute("connection", connectionService.getById(Long.parseLong(id)));
		}
		return "app/connection-view";
	}

	@RequestMapping(value = "/edit",
			method = RequestMethod.GET)
	public String edit(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id,
			@RequestParam(value="consumerId", required = false) String consumerId) throws Exception{

		System.out.println("Got edit request for connection id " + id);
		if(consumerId != null) {
			model.addAttribute("connection", connectionService.getByConsumerId(Long.parseLong(consumerId)));
		} else {
			model.addAttribute("connection", connectionService.getById(Long.parseLong(id)));
		}
		model.addAttribute("consumerList", consumerService.getAll());
		model.addAttribute("header", "Edit Connection");
		return "app/connection-create";
	}

	@RequestMapping(value = "/delete",
			method = RequestMethod.GET)
	public String delete(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got delete request for connection id " + id);

		connectionService.deleteById(Long.parseLong(id));
		redirectAttributes.addFlashAttribute("successMessage", "Connection with id " + id + " deleted successfully!");
		return "redirect:/connection";
	}

}
