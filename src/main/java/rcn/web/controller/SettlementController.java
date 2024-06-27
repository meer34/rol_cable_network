package rcn.web.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import rcn.web.model.AppUser;
import rcn.web.model.Settlement;
import rcn.web.service.AppUserService;
import rcn.web.service.SettlementService;

@Controller
@RequestMapping("/settlement")
public class SettlementController {

	@Value("${INITIAL_PAGE_SIZE}") private Integer initialPageSize;
	@Autowired SettlementService settlementService;
	@Autowired AppUserService appUserService;

	@GetMapping
	public String showBasePage(Model model,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			@RequestParam(value="keyword", required = false) String keyword,
			@RequestParam(value="appUserId", required = false) Long appUserId) {

		Page<Settlement> listPage = null;

		if(keyword == null) {
			System.out.println("Settlement home page");
			if(appUserId == null) listPage = settlementService.getAll(page.orElse(1) - 1, size.orElse(initialPageSize));
			else listPage = settlementService.getPageByAppUser(appUserId, page.orElse(1) - 1, size.orElse(initialPageSize));

		} else {
			System.out.println("Searching Settlements for keyword:" + keyword);
			//TODO
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

		return "app/settlement";

	}

	@GetMapping("/add")
	public String addSettlement (Model model, Settlement settlement, 
			@RequestParam(value="appUserId", required = false) Long appUserId) {
		
		List<AppUser> appUsers = appUserService.getAllAppUsers();
		
		model.addAttribute("header", "Settle Amount");
		model.addAttribute("appUsers", appUsers.stream()
											   .filter(appUser -> !"ADMIN".equals(appUser.getUserType()))
											   .collect(Collectors.toList()));
		model.addAttribute("admins", appUsers.stream()
											 .filter(appUser -> "ADMIN".equals(appUser.getUserType()))
											 .collect(Collectors.toList()));
		model.addAttribute("appUserId", appUserId);
		
		return "app/settlement-create";
	}
	
	@RequestMapping(value = "/save",
			method = RequestMethod.POST)
	public String saveSettlement(Model model, Settlement settlement, RedirectAttributes redirectAttributes) throws Exception{
		
		settlementService.save(settlement);
		
		redirectAttributes.addFlashAttribute("successMessage", "Settlement from " + settlement.getSettledBy().getName() + " saved successfully!");
		return "redirect:/settlement";

	}

	@RequestMapping(value = "/view",
			method = RequestMethod.GET)
	public String view(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id) throws Exception{

		System.out.println("Got view request for settlement id " + id);
		model.addAttribute("header", " View Settlement");
		model.addAttribute("settlement", settlementService.getById(Long.parseLong(id)));
		return "app/settlement-view";
	}
	
	@RequestMapping(value = "/edit",
			method = RequestMethod.GET)
	public String edit(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id) throws Exception{

		System.out.println("Got edit request for settlement id " + id);
		Settlement settlement = settlementService.getById(Long.parseLong(id));
		
		List<AppUser> appUsers = appUserService.getAllAppUsers();
		model.addAttribute("header", " Edit Settlement");
		model.addAttribute("appUsers", appUsers.stream()
											   .filter(appUser -> !"ADMIN".equals(appUser.getUserType()))
											   .collect(Collectors.toList()));
		model.addAttribute("admins", appUsers.stream()
											 .filter(appUser -> "ADMIN".equals(appUser.getUserType()))
											 .collect(Collectors.toList()));
		
		model.addAttribute("settlement", settlement);
		return "app/settlement-create";
	}

	@RequestMapping(value = "/delete",
			method = RequestMethod.GET)
	public String delete(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got delete request for settlement id " + id);

		settlementService.deleteById(Long.parseLong(id));
		redirectAttributes.addFlashAttribute("successMessage", "Settlement with id " + id + " deleted successfully!");
		return "redirect:/settlement";
	}
	
	@RequestMapping(value = "/user",
			method = RequestMethod.GET)
	public String userSettlemets(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id) throws Exception{

		System.out.println("Settlement user page");

		model.addAttribute("appUserList", 
				appUserService.getAllAppUsers().stream()
				.filter(appUser -> "APP_USER".equals(appUser.getUserType()))
				.collect(Collectors.toList()));

		return "app/settlement-user";

	}

}
