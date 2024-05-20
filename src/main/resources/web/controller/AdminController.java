package com.hunter.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hunter.web.model.Admin;
import com.hunter.web.model.User;
import com.hunter.web.repo.UserRepo;
import com.hunter.web.service.AdminService;
import com.hunter.web.service.ModeratorService;

@Controller
public class AdminController {

	@Autowired AdminService adminService;
	@Autowired ModeratorService moderatorService;
	@Autowired UserRepo userRepository;

	@GetMapping("/admin")
	public String showAdminPage(Model model) {
		model.addAttribute("adminList", adminService.getAllUsers());
		return "admin";
	}

	@GetMapping("/addAdminPage")
	public String showAddAdminPage(Model model) {
		model.addAttribute("admin", new Admin());
		model.addAttribute("header", "Create Admin");
		return "admin-create";
	}

	@RequestMapping(value = "/createAdmin",
			method = RequestMethod.POST)
	public String createAdmin(Model model, Admin admin, 
			RedirectAttributes redirectAttributes) throws Exception{

		if(admin.getId() == null) {
			admin.setUser(new User(admin.getName(), admin.getPhone(), true, "ADMIN"));
			admin = adminService.saveUserToDB(admin);

		} else {
			Admin tempAdmin = adminService.findUserById(admin.getId());

			tempAdmin.setName(admin.getName());
			tempAdmin.setPhone(admin.getPhone());
			tempAdmin.setAddress(admin.getAddress());

			tempAdmin.getUser().setUsername(admin.getName());
			tempAdmin.getUser().setPhone(admin.getPhone());

			admin = adminService.saveUserToDB(tempAdmin);
		}

		redirectAttributes.addFlashAttribute("successMessage", "New user " + admin.getName() + " added successfully as Admin!");
		return "redirect:/admin";

	}

	@RequestMapping(value = "/viewAdmin",
			method = RequestMethod.GET)
	public String viewAdmin(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got view request for admin id " + id);

		model.addAttribute("admin", adminService.findUserById(Long.parseLong(id)));
		return "view-admin";
	}

	@RequestMapping(value = "/editAdmin",
			method = RequestMethod.GET)
	public String editAdmin(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got edit request for admin id " + id);

		model.addAttribute("admin", adminService.findUserById(Long.parseLong(id)));
		model.addAttribute("header", "Edit Admin");
		return "admin-create";
	}
	
	@RequestMapping(value = "/deleteAdmin",
			method = RequestMethod.GET)
	public String deleteAdmin(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got delete request for admin id " + id);

		adminService.deleteUserById(Long.parseLong(id));
		redirectAttributes.addFlashAttribute("successMessage", "Admin with id " + id + " deleted successfully!");
		return "redirect:/admin";
	}

	@GetMapping("/checkIfNumberExistsForOtherAdmins")
	@ResponseBody
	public String checkIfNumberExistsForOtherAdmins(@RequestParam String phone, @RequestParam Long id) {
		
		if(adminService.getAdminsByPhoneNumberAndIdNotMatching(phone, id).size() > 0 ||
				moderatorService.getModeratorsByPhoneNumberAndIdNotMatching(phone, 0L).size() > 0) {
			return "Exist";
		}
		
		return "Not Exist";
		
	}
	
	@GetMapping("/checkIfNumberExistsForOtherModerators")
	@ResponseBody
	public String checkIfNumberExistsForOtherModerators(@RequestParam String phone, @RequestParam Long id) {
		
		if(moderatorService.getModeratorsByPhoneNumberAndIdNotMatching(phone, id).size() > 0 ||
				adminService.getAdminsByPhoneNumberAndIdNotMatching(phone, 0L).size() > 0) {
			return "Exist";
		}
		
		return "Not Exist";
		
	}
	
}
