package rcn.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import rcn.security.User;
import rcn.security.UserRepo;

@Controller
public class PasswordChangeController {

	@Autowired UserRepo userRepo;
	
	@GetMapping("/change-password")
	public String showChangePasswordPage(Model model, @RequestParam String phone) {
		model.addAttribute("phone", phone);
		return "change-password";
	}
	
	@GetMapping("/saveChangedPassword")
	public String saveChangedPassword(RedirectAttributes redirectAttributes, @RequestParam String phone, 
			@RequestParam String oldPassword, @RequestParam String newPassword) {
		
			User user = userRepo.getUserByPhoneNumber(phone);
			redirectAttributes.addAttribute("phone", phone);
			if(user != null) {
				BCryptPasswordEncoder b = new BCryptPasswordEncoder();
				
				if(b.matches(oldPassword, user.getPassword()) ) {
					if (b.matches(newPassword, user.getPassword()) ){
						redirectAttributes.addFlashAttribute("errorMessage", "New password is same as your old password!");
						
					} else {
						user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
						userRepo.save(user);
						redirectAttributes.addFlashAttribute("successMessage", "Password Changed Successfully!");
						return "redirect:/home";
					}
					
				} else {
					redirectAttributes.addFlashAttribute("errorMessage", "Old Password Does not Match!");
				}
				
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "User Not Found with phone number:" + phone);
			}

			return "redirect:/change-password";
	}

}
