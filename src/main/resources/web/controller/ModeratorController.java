package com.hunter.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hunter.web.model.Moderator;
import com.hunter.web.model.User;
import com.hunter.web.service.ModeratorService;

@Controller
public class ModeratorController {

	@Autowired ModeratorService moderatorService;

	@GetMapping("/moderator")
	public String showModeratorPage(Model model) {
		model.addAttribute("moderatorList", moderatorService.getAllUsers());
		return "moderator";
	}

	@GetMapping("/addModeratorPage")
	public String showAddModeratorPage(Model model) {
		model.addAttribute("moderator", new Moderator());
		model.addAttribute("header", "Create Moderator");
		return "moderator-create";
	}

	@RequestMapping(value = "/createModerator",
			method = RequestMethod.POST)
	public String createModerator(Model model, Moderator moderator, 
			RedirectAttributes redirectAttributes) throws Exception{
		
		if(moderator.getId() == null) {
			moderator.setUser(new User(moderator.getName(), moderator.getPhone(), true, "MODERATOR"));
			moderator = moderatorService.saveUserToDB(moderator);
			
		} else {
			Moderator tempModerator = moderatorService.findUserById(moderator.getId());
			
			tempModerator.setName(moderator.getName());
			tempModerator.setPhone(moderator.getPhone());
			tempModerator.setAddress(moderator.getAddress());
			
			tempModerator.getUser().setUsername(moderator.getName());
			tempModerator.getUser().setPhone(moderator.getPhone());
			
			moderator = moderatorService.saveUserToDB(tempModerator);
		}

		redirectAttributes.addFlashAttribute("successMessage", "New user " + moderator.getName() + " added successfully as Moderator!");
		return "redirect:/moderator";

	}

	@RequestMapping(value = "/viewModerator",
			method = RequestMethod.GET)
	public String viewModerator(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got view request for moderator id " + id);
		model.addAttribute("moderator", moderatorService.findUserById(Long.parseLong(id)));
		return "view-moderator";
		
	}
	
	@RequestMapping(value = "/editModerator",
			method = RequestMethod.GET)
	public String editModerator(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{
		
		System.out.println("Got edit request for moderator id " + id);
		model.addAttribute("moderator", moderatorService.findUserById(Long.parseLong(id)));
		model.addAttribute("header", "Edit Moderator");
		return "moderator-create";
		
	}
	
	@RequestMapping(value = "/deleteModerator",
			method = RequestMethod.GET)
	public String deleteModerator(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got delete request moderator for id " + id);
		moderatorService.deleteUserById(Long.parseLong(id));
		redirectAttributes.addFlashAttribute("successMessage", "Moderator with id " + id + " deleted successfully!");
		return "redirect:/moderator";
		
	}
	
}
