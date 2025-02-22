package com.hunter.web.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hunter.web.service.ReminderService;

@Controller
public class AccessController {
	
	@Autowired ReminderService reminderService;
	
	@RequestMapping("/login")
	public String login() {
		return "index";
	}
	
	@RequestMapping("/accessDenied")
	public String accessDenied() {
		return "access-denied";
	}

}

