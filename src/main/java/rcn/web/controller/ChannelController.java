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
import rcn.web.model.Channel;
import rcn.web.service.ConsumerService;
import rcn.web.service.SubscriptionService;

@Controller
@RequestMapping("/channel")
@Slf4j
public class ChannelController {

	@Value("${INITIAL_PAGE_SIZE}") private Integer initialPageSize;
	@Autowired SubscriptionService subscriptionService;
	@Autowired ConsumerService consumerService;

	@GetMapping
	public String showBasePage(Model model,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			@RequestParam(value="keyword", required = false) String keyword) {

		Page<Channel> listPage = null;

		if(keyword == null) {
			log.info("Channel home page");
			listPage = subscriptionService.getAllChannels(page.orElse(1) - 1, size.orElse(initialPageSize));

		} else {
			log.info("Searching Channel for keyword:" + keyword);
			listPage = subscriptionService.searchChannelByKeyword(keyword, page.orElse(1) - 1, size.orElse(initialPageSize));

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

		return "app/channel";

	}

	@GetMapping("/add")
	@PreAuthorize("hasAnyAuthority('ADMIN','ADD_CHANNEL')")
	public String add(Model model, Channel channel) {
		log.info("Channel add page");
		model.addAttribute("header", "Create Channel");
		return "app/channel-create";
	}

	@RequestMapping(value = "/save",
			method = RequestMethod.POST)
	public String save(Model model, Channel channel, RedirectAttributes redirectAttributes) throws Exception{
		channel = subscriptionService.saveChannel(channel);
		redirectAttributes.addFlashAttribute("successMessage", "Channel " + channel.getName() + " saved successfully!");
		log.info("Channel " + channel.getName() + " saved successfully!");
		return "redirect:/channel";

	}

	@RequestMapping(value = "/view",
			method = RequestMethod.GET)
	public String view(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id) throws Exception{

		log.info("View request for channel id " + id);
		model.addAttribute("channel", subscriptionService.getChannelById(Long.parseLong(id)));
		return "app/channel-view";
	}

	@RequestMapping(value = "/edit",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String edit(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id) throws Exception{

		log.info("Edit request for channel id " + id);
		model.addAttribute("channel", subscriptionService.getChannelById(Long.parseLong(id)));
		model.addAttribute("header", "Edit Channel");
		return "app/channel-create";
	}

	@RequestMapping(value = "/delete",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String delete(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("id") String id) throws Exception{

		log.info("Delete request for channel id " + id);

		subscriptionService.deleteChannelById(Long.parseLong(id));
		redirectAttributes.addFlashAttribute("successMessage", "Channel with id " + id + " deleted successfully!");
		return "redirect:/channel";
	}

}
