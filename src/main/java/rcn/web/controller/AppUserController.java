package rcn.web.controller;

import java.util.List;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;
import rcn.security.Role;
import rcn.security.User;
import rcn.web.model.AppUser;
import rcn.web.repo.RoleRepo;
import rcn.web.service.AppUserService;

@Controller
@Slf4j
public class AppUserController {

	@Autowired AppUserService appUserService;
	@Autowired RoleRepo roleRepo;

	@GetMapping("/appUser")
	public String showAppUserPage(Model model) {
		log.info("App User home page");
		model.addAttribute("appUserList", 
				appUserService.getAllAppUsers().stream()
				.filter(appUser -> "APP_USER".equals(appUser.getUserType()) & appUser.getUser().getEnabled())
				.collect(Collectors.toList()));
		return "app-user";
	}
	
	@GetMapping("/disabledAppUser")
	public String showDisabledAppUserPage(Model model) {
		log.info("Disable App User");
		model.addAttribute("appUserList", 
				appUserService.getAllAppUsers().stream()
				.filter(appUser -> "APP_USER".equals(appUser.getUserType()) & !appUser.getUser().getEnabled())
				.collect(Collectors.toList()));
		return "disabled-app-user";
	}

	@GetMapping("/addAppUserPage")
	@PreAuthorize("hasAuthority('ADMIN')")
	public String showAddAppUserPage(Model model) {
		log.info("Create App User page");
		model.addAttribute("appUser", new AppUser());
		model.addAttribute("header", "Create App User");
		return "app-user-create";
	}

	@RequestMapping(value = "/createAppUser",
			method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String createAppUser(Model model, AppUser appUser, 
			RedirectAttributes redirectAttributes) throws Exception{
		
		log.info("Saving admin data");
		
		if(appUser.getId() == null) {
			log.info("New App User being created");
			User user = new User(appUser.getName(), appUser.getPhone(), true, null);
			user.setPassword(new BCryptPasswordEncoder().encode("Rcnuser@1234"));
			appUser.setUser(user);
			if(appUser.getRoles() != null) {
				for (String role : appUser.getRoles()) {
					appUser.getUser().getRoles().add(new Role(role));
				}
			}
			
			appUser.setUserType("APP_USER");
			appUser = appUserService.saveAppUserToDB(appUser);
			
			log.info("New App User " + appUser.getName() + " created successfully!");
			redirectAttributes.addFlashAttribute("successMessage", "New App User " + appUser.getName() + " created successfully! Please login and change password. Default password generated:Rcnuser@1234");

		} else {
			log.info("App User data being updated for id - " + appUser.getId());
			AppUser tempAppUser = appUserService.findAppUserById(appUser.getId());

			tempAppUser.setName(appUser.getName());
			tempAppUser.setPhone(appUser.getPhone());
			tempAppUser.setAddress(appUser.getAddress());

			tempAppUser.getUser().setUsername(appUser.getName());
			tempAppUser.getUser().setPhone(appUser.getPhone());
			
			List<Long> roleIds = tempAppUser.getUser().getRoles()
									.stream()
									.map(Role::getId)
									.collect(Collectors.toList());
			
			tempAppUser.getUser().getRoles().clear();
			if(appUser.getRoles() != null) {
				for (String role : appUser.getRoles()) {
					tempAppUser.getUser().getRoles().add(new Role(role));
				}
			}

			appUser = appUserService.saveAppUserToDB(tempAppUser);
			roleRepo.deleteAllById(roleIds);
			
			log.info("Edited details for App User " + appUser.getName() + " saved successfully!");
			redirectAttributes.addFlashAttribute("successMessage", "Edited details for App User " + appUser.getName() + " saved successfully!");
		}

		return "redirect:/appUser";

	}

	@RequestMapping(value = "/viewAppUser",
			method = RequestMethod.GET)
	public String viewAppUser(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		log.info("View request for appUser id " + id);
		model.addAttribute("appUser", appUserService.findAppUserById(Long.parseLong(id)));
		return "view-app-user";

	}

	@RequestMapping(value = "/editAppUser",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String editAppUser(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		log.info("Edit request for appUser id " + id);
		model.addAttribute("appUser", appUserService.findAppUserById(Long.parseLong(id)));
		model.addAttribute("header", "Edit App User");
		return "app-user-create";

	}

	@RequestMapping(value = "/disableAppUser",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String disableAppUser(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		log.info("Disable request for appUser id " + id);
		AppUser appUser = appUserService.findAppUserById(Long.parseLong(id));
		appUser.getUser().setEnabled(false);
		appUserService.saveAppUserToDB(appUser);

		return "redirect:/appUser";

	}
	
	@RequestMapping(value = "/enableAppUser",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String enableAppUser(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		log.info("Enable request for appUser id " + id);
		AppUser appUser = appUserService.findAppUserById(Long.parseLong(id));
		appUser.getUser().setEnabled(true);
		appUserService.saveAppUserToDB(appUser);

		return "redirect:/appUser";

	}
	
}
