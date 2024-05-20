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

import rcn.web.model.Bucket;
import rcn.web.service.ConsumerService;
import rcn.web.service.SubscriptionService;

@Controller
@RequestMapping("/bucket")
public class BucketController {

	@Value("${INITIAL_PAGE_SIZE}") private Integer initialPageSize;
	@Autowired SubscriptionService subscriptionService;
	@Autowired ConsumerService consumerService;

	@GetMapping
	public String showBasePage(Model model,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			@RequestParam(value="keyword", required = false) String keyword) {

		Page<Bucket> listPage = null;

		if(keyword == null) {
			System.out.println("Bucket home page");
			listPage = subscriptionService.getAllBuckets(page.orElse(1) - 1, size.orElse(initialPageSize));

		} else {
			System.out.println("Searching Bucket for keyword:" + keyword);
			listPage = subscriptionService.searchBucketByKeyword(keyword, page.orElse(1) - 1, size.orElse(initialPageSize));

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

		return "app/bucket";

	}

	@GetMapping("/add")
	public String add(Model model, Bucket bucket) {
		model.addAttribute("header", "Create Bucket");
		return "app/bucket-create";
	}

	@RequestMapping(value = "/save",
			method = RequestMethod.POST)
	public String save(Model model, Bucket bucket, RedirectAttributes redirectAttributes) throws Exception{
		bucket = subscriptionService.saveBucket(bucket);
		redirectAttributes.addFlashAttribute("successMessage", "Bucket " + bucket.getName() + " saved successfully!");
		return "redirect:/bucket";

	}

	@RequestMapping(value = "/view",
			method = RequestMethod.GET)
	public String view(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id) throws Exception{

		System.out.println("Got view request for connection id " + id);
		model.addAttribute("bucket", subscriptionService.getBucketById(Long.parseLong(id)));
		return "app/bucket-view";
	}

	@RequestMapping(value = "/edit",
			method = RequestMethod.GET)
	public String edit(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id) throws Exception{

		System.out.println("Got edit request for connection id " + id);
		model.addAttribute("bucket", subscriptionService.getBucketById(Long.parseLong(id)));
		model.addAttribute("header", "Edit Bucket");
		return "app/bucket-create";
	}

	@RequestMapping(value = "/delete",
			method = RequestMethod.GET)
	public String delete(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got delete request for connection id " + id);

		subscriptionService.deleteBucketById(Long.parseLong(id));
		redirectAttributes.addFlashAttribute("successMessage", "Bucket with id " + id + " deleted successfully!");
		return "redirect:/bucket";
	}

}
