package rcn.web.controller;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import rcn.security.User;
import rcn.security.UserRepo;
import rcn.web.model.AppUser;
import rcn.web.service.AppUserService;

@Controller
public class AdminController {

	@Autowired AppUserService appUserService;
	@Autowired UserRepo userRepository;

	@GetMapping("/admin")
	public String showAdminPage(Model model) {
		model.addAttribute("adminList", 
				appUserService.getAllAppUsers().stream()
				.filter(appUser -> "ADMIN".equals(appUser.getUserType()))
				.collect(Collectors.toList()));
		return "admin";
	}

	@GetMapping("/addAdminPage")
	@PreAuthorize("hasAuthority('ADMIN')")
	public String showAddAdminPage(Model model) {
		model.addAttribute("admin", new AppUser());
		model.addAttribute("header", "Create Admin");
		return "admin-create";
	}

	@RequestMapping(value = "/createAdmin",
			method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String createAdmin(Model model, AppUser appUser, 
			RedirectAttributes redirectAttributes) throws Exception{

		if(appUser.getId() == null) {
			User user = new User(appUser.getName(), appUser.getPhone(), true, "ADMIN");
			user.setPassword(new BCryptPasswordEncoder().encode("Rcnuser@1234"));
			appUser.setUser(user);
			appUser.setUserType("ADMIN");
			appUser = appUserService.saveAppUserToDB(appUser);
			redirectAttributes.addFlashAttribute("successMessage", "New user " + appUser.getName() + " added successfully as Admin User! Default password:Rcnuser@1234");
			
		} else {
			AppUser tempAppUser = appUserService.findAppUserById(appUser.getId());

			tempAppUser.setName(appUser.getName());
			tempAppUser.setPhone(appUser.getPhone());
			tempAppUser.setAddress(appUser.getAddress());
			appUser.setUserType("ADMIN");
			
			tempAppUser.getUser().setUsername(appUser.getName());
			tempAppUser.getUser().setPhone(appUser.getPhone());

			appUser = appUserService.saveAppUserToDB(tempAppUser);
			
			redirectAttributes.addFlashAttribute("successMessage", "Edited details for Admin User " + appUser.getName() + " saved successfully!");
		}

		return "redirect:/admin";

	}

	@RequestMapping(value = "/viewAdmin",
			method = RequestMethod.GET)
	public String viewAdmin(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got view request for admin id " + id);

		model.addAttribute("admin", appUserService.findAppUserById(Long.parseLong(id)));
		return "view-admin";
	}

	@RequestMapping(value = "/editAdmin",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String editAdmin(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got edit request for admin id " + id);

		model.addAttribute("admin", appUserService.findAppUserById(Long.parseLong(id)));
		model.addAttribute("header", "Edit Admin Details");
		return "admin-create";
	}
	
	@RequestMapping(value = "/deleteAdmin",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String deleteAdmin(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got delete request for admin id " + id);

		appUserService.deleteAppUserById(Long.parseLong(id));
		redirectAttributes.addFlashAttribute("successMessage", "Admin with id " + id + " deleted successfully!");
		return "redirect:/admin";
	}

	@GetMapping("/checkIfNumberExistsForOtherAppUsers")
	@ResponseBody
	public String checkIfNumberExistsForOtherAppUsers(@RequestParam String phone, @RequestParam Long id) {
		
		if(id == 0L) {
			if(appUserService.getAppUsersByPhoneNumber(phone).size() > 0) {
				return "Exist";
			}
		} else {
			if(appUserService.getAppUsersByPhoneNumberAndIdNotMatching(phone, id).size() > 0) {
				return "Exist";
			}
		}
		
		return "Not Exist";
		
	}
	
}
