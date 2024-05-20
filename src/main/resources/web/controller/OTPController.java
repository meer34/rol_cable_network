package com.hunter.web.controller;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hunter.web.model.Admin;
import com.hunter.web.model.Moderator;
import com.hunter.web.model.User;
import com.hunter.web.repo.AdminRepo;
import com.hunter.web.repo.ModeratorRepo;
import com.hunter.web.repo.UserRepo;
import com.hunter.web.util.AppUtility;

@Controller
public class OTPController {

	@Autowired UserRepo userRepo;
	@Autowired AdminRepo adminRepo;
	@Autowired ModeratorRepo moderatorRepo;
	@Autowired AppUtility utility;
	
	@GetMapping("/generatePin")
	@ResponseBody 
	public String showCreateItemPage(HttpServletResponse response, @RequestParam String phone) {
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			String otp= new DecimalFormat("0000").format(new Random().nextInt(9999));
			
			User user = userRepo.getUserByPhoneNumber(phone);
			if(user != null && phone.equals(user.getPhone())) {
				user.setPin(new BCryptPasswordEncoder().encode(otp));
				user.setPinGenerationTime(new Date());
				userRepo.save(user);
				
				Moderator moderator = moderatorRepo.findByUser(user).orElse(null);
				if(moderator != null) {
					moderator.setSynced(false);
					moderatorRepo.save(moderator);
				} else {
					Admin admin = adminRepo.findByUser(user).orElse(null);
					if(admin != null) {
						admin.setSynced(false);
						adminRepo.save(admin);
					}
				}
				
			} else {
				System.out.println("User Not Found with phone number:" + phone);
				return "User Not Found";
			}

			return utility.sendSMS(phone, "Your login OTP is - " + otp);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "Request Failed";
		}

	}

}
