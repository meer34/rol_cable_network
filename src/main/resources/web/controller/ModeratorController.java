package com.hunter.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hunter.web.model.App User;
import com.hunter.web.model.User;
import com.hunter.web.service.appUserService;

@Controller
public class App UserController {

	@Autowired appUserService appUserService;

	@GetMapping("/appUser")
	public String showApp UserPage(Model model) {
		model.addAttribute("appUserList", appUserService.getAllUsers());
		return "app-user";
	}

	@GetMapping("/addApp UserPage")
	public String showAddApp UserPage(Model model) {
		model.addAttribute("appUser", new App User());
		model.addAttribute("header", "Create App User");
		return "app-user-create";
	}

	@RequestMapping(value = "/createAppUser",
			method = RequestMethod.POST)
	public String createAppUser(Model model, App User appUser, 
			RedirectAttributes redirectAttributes) throws Exception{
		
		if(appUser.getId() == null) {
			appUser.setUser(new User(appUser.getName(), appUser.getPhone(), true, "MODERATOR"));
			appUser = appUserService.saveUserToDB(appUser);
			
		} else {
			App User tempAppUser = appUserService.findUserById(appUser.getId());
			
			tempAppUser.setName(appUser.getName());
			tempAppUser.setPhone(appUser.getPhone());
			tempAppUser.setAddress(appUser.getAddress());
			
			tempAppUser.getUser().setUsername(appUser.getName());
			tempAppUser.getUser().setPhone(appUser.getPhone());
			
			appUser = appUserService.saveUserToDB(tempAppUser);
		}

		redirectAttributes.addFlashAttribute("successMessage", "New user " + appUser.getName() + " added successfully as App User!");
		return "redirect:/appUser";

	}

	@RequestMapping(value = "/viewAppUser",
			method = RequestMethod.GET)
	public String viewAppUser(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got view request for appUser id " + id);
		model.addAttribute("appUser", appUserService.findUserById(Long.parseLong(id)));
		return "view-appUser";
		
	}
	
	@RequestMapping(value = "/editAppUser",
			method = RequestMethod.GET)
	public String editAppUser(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{
		
		System.out.println("Got edit request for appUser id " + id);
		model.addAttribute("appUser", appUserService.findUserById(Long.parseLong(id)));
		model.addAttribute("header", "Edit App User");
		return "app-user-create";
		
	}
	
	@RequestMapping(value = "/deleteAppUser",
			method = RequestMethod.GET)
	public String deleteAppUser(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got delete request appUser for id " + id);
		appUserService.deleteUserById(Long.parseLong(id));
		redirectAttributes.addFlashAttribute("successMessage", "App User with id " + id + " deleted successfully!");
		return "redirect:/appUser";
		
	}
	
}
