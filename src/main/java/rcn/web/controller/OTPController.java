package rcn.web.controller;

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

import rcn.security.User;
import rcn.security.UserRepo;
import rcn.web.repo.AdminRepo;
import rcn.web.repo.ModeratorRepo;
import rcn.web.util.AppUtility;

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
