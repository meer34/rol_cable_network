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

import rcn.web.model.Collection;
import rcn.web.model.Consumer;
import rcn.web.service.AppUserService;
import rcn.web.service.CollectionService;
import rcn.web.service.ConsumerService;

@Controller
@RequestMapping("/collection")
public class CollectionController {

	@Value("${INITIAL_PAGE_SIZE}") private Integer initialPageSize;
	@Autowired CollectionService collectionService;
	@Autowired ConsumerService consumerService;
	@Autowired AppUserService appUserService;

	@GetMapping
	public String showBasePage(Model model,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			@RequestParam(value="keyword", required = false) String keyword,
			@RequestParam(value="consumerId", required = false) Long consumerId) {

		Page<Collection> listPage = null;

		if(keyword == null) {
			System.out.println("Bucket home page");
			if(consumerId == null) listPage = collectionService.getAll(page.orElse(1) - 1, size.orElse(initialPageSize));
			else listPage = collectionService.getPageByConsumer(consumerId, page.orElse(1) - 1, size.orElse(initialPageSize));

		} else {
			System.out.println("Searching Collection for keyword:" + keyword);
//			listPage = collectionService.searchAllByKeyword(keyword, page.orElse(1) - 1, size.orElse(initialPageSize));

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

		return "app/collection";

	}

	@GetMapping("/collectSubscriptionDue")
	public String collectSubscriptionDue(Model model, Collection collection, 
			@RequestParam(value="consumerId", required = true) Long consumerId,
			@RequestParam(value="action", required = true) String action) {
		model.addAttribute("header", "Collect Subscription Due, Pending Amount - ");
		model.addAttribute("consumerList", consumerService.getAll());
		model.addAttribute("users", appUserService.getAllAppUsers());
		model.addAttribute("consumerId", consumerId);
		
		if(action.equals("Collect Subscription Due")) {
			model.addAttribute("billType", "subscription");
		}
		
		Consumer consumer = consumerService.getById(consumerId);
		consumer.calculateTotalSubscriptionBill();
		model.addAttribute("pendingAmount", consumer.getSubscriptionBill());
		
		return "app/collection-create";
	}
	
	@GetMapping("/collectOtherDue")
	public String collectOtherDue(Model model, Collection collection,
			@RequestParam(value="consumerId", required = true) Long consumerId,
			@RequestParam(value="action", required = true) String action) {
		model.addAttribute("header", "Collect Other Due, Pending Amount - ");
		model.addAttribute("consumerList", consumerService.getAll());
		model.addAttribute("users", appUserService.getAllAppUsers());
		model.addAttribute("consumerId", consumerId);
		
		if(action.equals("Collect Other Due")) {
			model.addAttribute("billType", "otherDue");
		}
		
		Consumer consumer = consumerService.getById(consumerId);
		consumer.calculateTotalOtherDueBill();
		model.addAttribute("pendingAmount", consumer.getOtherDueBill());
		return "app/collection-create";
	}

	@RequestMapping(value = "/saveSubscriptionCollection",
			method = RequestMethod.POST)
	public String saveSubscriptionCollection(Model model, Collection collection, RedirectAttributes redirectAttributes) throws Exception{
		collection.setBillType("Subscription");
		collection = collectionService.save(collection);
		
		Consumer consumer = collection.getConsumer();
		consumer.setTotalPaid(consumer.getTotalPaid() + collection.getAmount());
		consumerService.save(consumer);
		
		redirectAttributes.addFlashAttribute("successMessage", "Collection from " + collection.getConsumer().getFullName() + " saved successfully!");
		return "redirect:/collection";

	}
	
	@RequestMapping(value = "/saveOtherDueCollection",
			method = RequestMethod.POST)
	public String saveOtherDueCollection(Model model, Collection collection, RedirectAttributes redirectAttributes) throws Exception{
		collection.setBillType("Other Due");
		collection = collectionService.save(collection);
		
		Consumer consumer = collection.getConsumer();
		consumer.setTotalPaid(consumer.getTotalPaid() + collection.getAmount());
		consumerService.save(consumer);
		
		redirectAttributes.addFlashAttribute("successMessage", "Collection from " + collection.getConsumer().getFullName() + " saved successfully!");
		return "redirect:/collection";

	}

	@RequestMapping(value = "/view",
			method = RequestMethod.GET)
	public String view(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id) throws Exception{

		System.out.println("Got view request for collection id " + id);
		model.addAttribute("collection", collectionService.getById(Long.parseLong(id)));
		return "app/collection-view";
	}
	
	@RequestMapping(value = "/edit",
			method = RequestMethod.GET)
	public String edit(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id) throws Exception{

		System.out.println("Got edit request for collection id " + id);
		Collection collection = collectionService.getById(Long.parseLong(id));
		
		if(collection.getBillType().equals("Subscription")) {
			model.addAttribute("header", "Edit Subscription Collection for - " + collection.getConsumer().getFullName());
		} else {
			model.addAttribute("header", "Edit Other Due Collection for - " + collection.getConsumer().getFullName());
		}
		model.addAttribute("collection", collection);
		model.addAttribute("consumerList", consumerService.getAll());
		model.addAttribute("users", appUserService.getAllAppUsers());
		model.addAttribute("consumerId", collection.getConsumer().getId());
		return "app/collection-create";
	}

	@RequestMapping(value = "/delete",
			method = RequestMethod.GET)
	public String delete(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got delete request for collection id " + id);

		collectionService.deleteById(Long.parseLong(id));
		redirectAttributes.addFlashAttribute("successMessage", "Collection with id " + id + " deleted successfully!");
		return "redirect:/collection";
	}

}
