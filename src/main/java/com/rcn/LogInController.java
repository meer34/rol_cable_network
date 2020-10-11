package com.rcn;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LogInController {
	
	@RequestMapping("rcn")
	public ModelAndView homePage1() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("errorMessage", "");
		mv.setViewName("index");
		return mv;
	}

	@RequestMapping("login")
	public ModelAndView login(@RequestParam("email") String email, @RequestParam("pass") String password) {
		ModelAndView mv = new ModelAndView();
		
		if("1234".equalsIgnoreCase(password)) {
			mv.addObject("email", email);
			mv.setViewName("welcome");
		} else {
			mv.addObject("errorMessage", "Incorrect credentials! Please try again.");
			mv.setViewName("index");
		}
		return mv;
	}

}