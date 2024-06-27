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

import rcn.web.model.ChannelPackage;
import rcn.web.service.ConsumerService;
import rcn.web.service.SubscriptionService;

@Controller
@RequestMapping("/channelPackage")
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
			System.out.println("Package home page");
			listPage = subscriptionService.getAllPackages(page.orElse(1) - 1, size.orElse(initialPageSize));

		} else {
			System.out.println("Searching Package for keyword:" + keyword);
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
	public String add(Model model, ChannelPackage pack) {
		model.addAttribute("header", "Create Package");
		return "app/package-create";
	}

	@RequestMapping(value = "/save",
			method = RequestMethod.POST)
	public String save(Model model, ChannelPackage pack, RedirectAttributes redirectAttributes) throws Exception{
		pack = subscriptionService.savePackage(pack);
		redirectAttributes.addFlashAttribute("successMessage", "Package " + pack.getName() + " saved successfully!");
		return "redirect:/channelPackage";

	}

	@RequestMapping(value = "/view",
			method = RequestMethod.GET)
	public String view(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id) throws Exception{

		System.out.println("Got view request for package id " + id);
		model.addAttribute("channelPackage", subscriptionService.getPackageById(Long.parseLong(id)));
		return "app/package-view";
	}

	@RequestMapping(value = "/edit",
			method = RequestMethod.GET)
	public String edit(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id) throws Exception{

		System.out.println("Got edit request for package id " + id);
		model.addAttribute("channelPackage", subscriptionService.getPackageById(Long.parseLong(id)));
		model.addAttribute("header", "Edit Package");
		return "app/package-create";
	}

	@RequestMapping(value = "/delete",
			method = RequestMethod.GET)
	public String delete(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got delete request for package id " + id);

		subscriptionService.deletePackageById(Long.parseLong(id));
		redirectAttributes.addFlashAttribute("successMessage", "Package with id " + id + " deleted successfully!");
		return "redirect:/channelPackage";
	}

}
