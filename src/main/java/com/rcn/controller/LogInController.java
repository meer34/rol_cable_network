package com.rcn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LogInController {

	@RequestMapping("/")
	public String homePage1() {
		return "home_admin";
	}

	@RequestMapping("/login")
	public String login() {
		return "index";
	}

	@RequestMapping("/add_customer")
	public String addCustomer() {
		return "add_customer";
	}

	@RequestMapping("/welcome")
	public String welcome() {
		return "welcome";
	}

}