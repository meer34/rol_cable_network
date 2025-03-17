package rcn.web.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import rcn.web.service.ReminderService;

@Controller
@Slf4j
public class AccessController {
	
	@Autowired ReminderService reminderService;
	
	@RequestMapping("/login")
	public String login() {
		log.info("Login request received");
		return "index";
	}
	
	@RequestMapping("/accessDenied")
	public String accessDenied() {
		log.info("Showing acess denied page!");
		return "access-denied";
	}

}

