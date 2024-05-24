package rcn.web.controller;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import rcn.security.Role;
import rcn.security.User;
import rcn.security.UserRepo;
import rcn.web.model.Admin;
import rcn.web.model.AppUser;
import rcn.web.service.AppUserService;

@Controller
public class AdminController {

	@Autowired AppUserService appUserService;
	@Autowired UserRepo userRepository;

	@GetMapping("/admin")
	public String showAdminPage(Model model) {
		model.addAttribute("adminList", 
				appUserService.getAllUsers().stream()
				.filter(appUser -> "ADMIN".equals(appUser.getUserType()))
				.collect(Collectors.toList()));
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
	public String createAdmin(Model model, AppUser appUser, 
			RedirectAttributes redirectAttributes) throws Exception{

		if(appUser.getId() == null) {
			appUser.setUser(new User(appUser.getName(), appUser.getPhone(), true, "ADMIN"));
			appUser.setUserType("ADMIN");
			appUser = appUserService.saveUserToDB(appUser);
			
		} else {
			AppUser tempAppUser = appUserService.findUserById(appUser.getId());

			tempAppUser.setName(appUser.getName());
			tempAppUser.setPhone(appUser.getPhone());
			tempAppUser.setAddress(appUser.getAddress());
			appUser.setUserType("ADMIN");
			
			tempAppUser.getUser().setUsername(appUser.getName());
			tempAppUser.getUser().setPhone(appUser.getPhone());

			appUser = appUserService.saveUserToDB(tempAppUser);
		}

		redirectAttributes.addFlashAttribute("successMessage", "New Admin user " + appUser.getName() + " added successfully as App User!");
		return "redirect:/admin";

	}

	@RequestMapping(value = "/viewAdmin",
			method = RequestMethod.GET)
	public String viewAdmin(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got view request for admin id " + id);

		model.addAttribute("admin", appUserService.findUserById(Long.parseLong(id)));
		return "view-admin";
	}

	@RequestMapping(value = "/editAdmin",
			method = RequestMethod.GET)
	public String editAdmin(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got edit request for admin id " + id);

		model.addAttribute("admin", appUserService.findUserById(Long.parseLong(id)));
		model.addAttribute("header", "Edit Admin Details");
		return "admin-create";
	}
	
	@RequestMapping(value = "/deleteAdmin",
			method = RequestMethod.GET)
	public String deleteAdmin(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got delete request for admin id " + id);

		appUserService.deleteUserById(Long.parseLong(id));
		redirectAttributes.addFlashAttribute("successMessage", "Admin with id " + id + " deleted successfully!");
		return "redirect:/admin";
	}

	@GetMapping("/checkIfNumberExistsForOtherAppUsers")
	@ResponseBody
	public String checkIfNumberExistsForOtherAppUsers(@RequestParam String phone, @RequestParam Long id) {
		
		if(appUserService.getAppUsersByPhoneNumberAndIdNotMatching(phone, id).size() > 0 ||
				appUserService.getAppUsersByPhoneNumberAndIdNotMatching(phone, 0L).size() > 0) {
			return "Exist";
		}
		
		return "Not Exist";
		
	}
	
}
