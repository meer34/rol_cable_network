package rcn.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import rcn.web.model.Consumer;
import rcn.web.model.Due;
import rcn.web.service.ConsumerService;
import rcn.web.service.DueService;

@Controller
@RequestMapping("/due")
public class DueController {

	@Value("${INITIAL_PAGE_SIZE}") private Integer initialPageSize;
	@Autowired ConsumerService consumerService;
	@Autowired DueService dueService;

	@GetMapping("/add")
	public String add(Model model, 
			@RequestParam(value="consumerId", required = false) String consumerId, Due due) {
		Consumer consumer = consumerService.getById(Long.parseLong(consumerId));
		model.addAttribute("header", "Add Due for " 
		+ consumer.getFullName() + ", Consumer Id: " + consumerId);
		model.addAttribute("consumerId", consumerId);
		return "app/due-create";
	}

	@RequestMapping(value = "/save",
			method = RequestMethod.POST)
	public String save(Model model, Due due, RedirectAttributes redirectAttributes) throws Exception{
		due = dueService.saveDue(due);
		redirectAttributes.addFlashAttribute("successMessage", "Due amount of " + due.getDueAmount() 
		+ " saved successfully for consumer: " + due.getConsumer().getFullName());
		redirectAttributes.addAttribute("consumerId", due.getConsumer().getId());
		return "redirect:/bill/getDueRecordsForConsumer";

	}

	@RequestMapping(value = "/edit",
			method = RequestMethod.GET)
	public String edit(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="dueId", required = false) String dueId) throws Exception{

		System.out.println("Got edit request for dueId " + dueId);
		Due due = dueService.getDueById(Long.parseLong(dueId));
		model.addAttribute("due", due);
		System.out.println("######" + due.toString());
		model.addAttribute("consumerId", due.getConsumer().getId());
		model.addAttribute("header", "Edit Due");
		return "app/due-create";
	}

	@RequestMapping(value = "/delete",
			method = RequestMethod.GET)
	public String delete(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("dueId") String dueId) throws Exception{

		System.out.println("Got delete request for dueId: " + dueId);
		Long consumerId = dueService.getDueById(Long.parseLong(dueId))
				.getConsumer()
				.getId();
		redirectAttributes.addAttribute("consumerId", consumerId);
		
		Due due = dueService.getDueById(Long.parseLong(dueId));
		
		if(due.getPaidAmount() == 0.0) {
			dueService.deleteDueById(Long.parseLong(dueId));
			redirectAttributes.addFlashAttribute("successMessage", "Due with id " + dueId + " deleted successfully for consumer id " + consumerId);
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete Due since it already has paid amount!");
		}
		
		return "redirect:/bill/getDueRecordsForConsumer";
	
	}

}
