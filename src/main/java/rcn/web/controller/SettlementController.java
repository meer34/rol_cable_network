package rcn.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import rcn.security.Role;
import rcn.security.User;
import rcn.web.model.AppUser;
import rcn.web.service.AppUserService;

@Controller
@RequestMapping("/settlement")
public class SettlementController {

	@Autowired AppUserService appUserService;

	public String basePage(Model model) {
		model.addAttribute("appUserList", appUserService.getAllAppUsers());
		return "app-user";
	}

	@GetMapping("/settleAmount")
	public String settleAmount(Model model,
			@RequestParam(value="consumerId", required = true) Long consumerId) {
		model.addAttribute("appUser", new AppUser());
		model.addAttribute("header", "Create App User");
		return "app-user-create";
	}

	@RequestMapping(value = "/createAppUser",
			method = RequestMethod.POST)
	public String createAppUser(Model model, AppUser appUser, 
			RedirectAttributes redirectAttributes) throws Exception{

		if(appUser.getId() == null) {
			//			System.out.println("$$$ - " + appUser.toString());
			appUser.setUser(new User(appUser.getName(), appUser.getPhone(), true, null));
			if(appUser.getRoles() != null) {
				for (String role : appUser.getRoles()) {
					appUser.getUser().getRoles().add(new Role(role));
				}
			}

			appUser = appUserService.saveAppUserToDB(appUser);

		} else {
			AppUser tempAppUser = appUserService.findAppUserById(appUser.getId());

			tempAppUser.setName(appUser.getName());
			tempAppUser.setPhone(appUser.getPhone());
			tempAppUser.setAddress(appUser.getAddress());

			tempAppUser.getUser().setUsername(appUser.getName());
			tempAppUser.getUser().setPhone(appUser.getPhone());

			tempAppUser.getUser().getRoles().clear();
			if(appUser.getRoles() != null) {
				for (String role : appUser.getRoles()) {
					tempAppUser.getUser().getRoles().add(new Role(role));
				}
			}

			appUser = appUserService.saveAppUserToDB(tempAppUser);
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
		model.addAttribute("appUser", appUserService.findAppUserById(Long.parseLong(id)));
		return "view-appUser";

	}

	@RequestMapping(value = "/editAppUser",
			method = RequestMethod.GET)
	public String editAppUser(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got edit request for appUser id " + id);
		model.addAttribute("appUser", appUserService.findAppUserById(Long.parseLong(id)));
		model.addAttribute("header", "Edit App User");
		return "app-user-create";

	}

	@RequestMapping(value = "/deleteAppUser",
			method = RequestMethod.GET)
	public String deleteAppUser(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got delete request appUser for id " + id);
		AppUser appUser = appUserService.findAppUserById(Long.parseLong(id));

		/*if(appUser != null) {
			if(appUser.getStockOutList().isEmpty()) {
				appUserService.deleteUserById(Long.parseLong(id));
				redirectAttributes.addFlashAttribute("successMessage", "App User with id " + id + " deleted successfully!");
			} else {
				redirectAttributes.addFlashAttribute("successMessage", appUser.getStockOutList().size() + " Stock Out entries present this App User!");
			}
		}*/

		return "redirect:/appUser";

	}

}
