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
import rcn.web.model.Bouquet;
import rcn.web.service.ConsumerService;
import rcn.web.service.SubscriptionService;

@Controller
@RequestMapping("/bouquet")
@Slf4j
public class BouquetController {

	@Value("${INITIAL_PAGE_SIZE}") private Integer initialPageSize;
	@Autowired SubscriptionService subscriptionService;
	@Autowired ConsumerService consumerService;

	@GetMapping
	public String showBasePage(Model model,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			@RequestParam(value="keyword", required = false) String keyword) {

		Page<Bouquet> listPage = null;

		if(keyword == null) {
			log.info("Bouquet home page");
			listPage = subscriptionService.getAllBouquets(page.orElse(1) - 1, size.orElse(initialPageSize));

		} else {
			log.info("Searching Bouquet for keyword:" + keyword);
			listPage = subscriptionService.searchBouquetByKeyword(keyword, page.orElse(1) - 1, size.orElse(initialPageSize));

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

		return "app/bouquet";

	}

	@GetMapping("/add")
	@PreAuthorize("hasAnyAuthority('ADMIN','ADD_BOUQUET')")
	public String add(Model model, Bouquet bouquet) {
		log.info("Add bouquet page");
		model.addAttribute("header", "Create Bouquet");
		return "app/bouquet-create";
	}

	@RequestMapping(value = "/save",
			method = RequestMethod.POST)
	public String save(Model model, Bouquet bouquet, RedirectAttributes redirectAttributes) throws Exception{
		
		bouquet = subscriptionService.saveBouquet(bouquet);
		
		redirectAttributes.addFlashAttribute("successMessage", "Bouquet " + bouquet.getName() + " saved successfully!");
		log.info("Bouquet " + bouquet.getName() + " saved successfully!");
		
		return "redirect:/bouquet";

	}

	@RequestMapping(value = "/view",
			method = RequestMethod.GET)
	public String view(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id) throws Exception{

		log.info("View request for bouquet id " + id);
		model.addAttribute("bouquet", subscriptionService.getBouquetById(Long.parseLong(id)));
		return "app/bouquet-view";
	}

	@RequestMapping(value = "/edit",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String edit(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id) throws Exception{

		log.info("Edit request for bouquet id " + id);
		model.addAttribute("bouquet", subscriptionService.getBouquetById(Long.parseLong(id)));
		model.addAttribute("header", "Edit Bouquet");
		return "app/bouquet-create";
	}

	@RequestMapping(value = "/delete",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String delete(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("id") String id) throws Exception{

		log.info("Delete request for bouquet id " + id);

		subscriptionService.deleteBouquetById(Long.parseLong(id));
		redirectAttributes.addFlashAttribute("successMessage", "Bouquet with id " + id + " deleted successfully!");
		return "redirect:/bouquet";
	}

}
