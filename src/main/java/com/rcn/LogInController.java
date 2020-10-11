package com.rcn;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LogInController {
	
	@RequestMapping("rcn")
	public String homePage() {
		return "index";
	}

}