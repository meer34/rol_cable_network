package rcn.web.controller;

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

import lombok.extern.slf4j.Slf4j;
import rcn.web.model.ChannelPackage;
import rcn.web.service.ConsumerService;
import rcn.web.service.SubscriptionService;

@Controller
@RequestMapping("/channelPackage")
@Slf4j
public class PackageController {

	@Value("${INITIAL_PAGE_SIZE}") private Integer initialPageSize;
	@Autowired SubscriptionService subscriptionService;
	@Autowired ConsumerService consumerService;

	@GetMapping
	public String showBasePage(Model model,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			@RequestParam(value="keyword", required = false) String keyword) {

		Page<ChannelPackage> listPage = null;

		if(keyword == null) {
			log.info("Package home page");
			listPage = subscriptionService.getAllPackages(page.orElse(1) - 1, size.orElse(initialPageSize));

		} else {
			log.info("Searching Package for keyword:" + keyword);
			listPage = subscriptionService.searchPackageByKeyword(keyword, page.orElse(1) - 1, size.orElse(initialPageSize));

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

		return "app/package-home";

	}

	@GetMapping("/add")
	@PreAuthorize("hasAnyAuthority('ADMIN','ADD_PACKAGE')")
	public String add(Model model, ChannelPackage pack) {
		log.info("Add package page");
		model.addAttribute("header", "Create Package");
		return "app/package-create";
	}

	@RequestMapping(value = "/save",
			method = RequestMethod.POST)
	public String save(Model model, ChannelPackage pack, RedirectAttributes redirectAttributes) throws Exception{
		
		pack = subscriptionService.savePackage(pack);
		
		redirectAttributes.addFlashAttribute("successMessage", "Package " + pack.getName() + " saved successfully!");
		log.info("Package " + pack.getName() + " saved successfully!");
		
		return "redirect:/channelPackage";

	}

	@RequestMapping(value = "/view",
			method = RequestMethod.GET)
	public String view(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id) throws Exception{

		log.info("View request for package id " + id);
		model.addAttribute("channelPackage", subscriptionService.getPackageById(Long.parseLong(id)));
		return "app/package-view";
	}

	@RequestMapping(value = "/edit",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String edit(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id) throws Exception{

		log.info("Edit request for package id " + id);
		model.addAttribute("channelPackage", subscriptionService.getPackageById(Long.parseLong(id)));
		model.addAttribute("header", "Edit Package");
		return "app/package-create";
	}

	@RequestMapping(value = "/delete",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String delete(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("id") String id) throws Exception{

		log.info("Delete request for package id " + id);

		subscriptionService.deletePackageById(Long.parseLong(id));
		redirectAttributes.addFlashAttribute("successMessage", "Package with id " + id + " deleted successfully!");
		return "redirect:/channelPackage";
	}

}
