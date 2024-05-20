package com.hunter.web.controller;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hunter.web.model.Reminder;
import com.hunter.web.service.ReminderService;
import com.hunter.web.util.AppUtility;

@Controller
@PropertySource("classpath:hunter_garments.properties")
public class ReminderController {

	@Autowired ReminderService reminderService;
	@Autowired AppUtility utility;

	@Value("${INITIAL_PAGE_SIZE}")
	private Integer initialPageSize;
	
	@RequestMapping("/reminder")
	public String reminder(Model model,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			@RequestParam(value="fromDate", required = false) String fromDate,
			@RequestParam(value="toDate", required = false) String toDate,
			@RequestParam(value="keyword", required = false) String keyword) {

		Page<Reminder> listPage = null;

		if(keyword == null && fromDate == null && toDate == null) {
			System.out.println("Reminder home page");
			listPage = reminderService.getAllReminders(page.orElse(1) - 1, size.orElse(initialPageSize));

		} else {
			System.out.println("Searching Reminder for fromDate:" + fromDate + " and toDate:" +toDate +" and keyword:" + keyword);
			listPage = reminderService.searchReminderByDateAndKeyword(keyword, fromDate, toDate, page.orElse(1) - 1, size.orElse(initialPageSize));

			model.addAttribute("fromDate", fromDate);
			model.addAttribute("toDate", toDate);
			model.addAttribute("keyword", keyword);

		}

		model.addAttribute("listPage", listPage);
		int totalPages = listPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
					.boxed()
					.collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		return "reminder";

	}

	@RequestMapping("/addNewReminderPage")
	public String addNewReminderPage(Model model) {
		model.addAttribute("header", "Edit Reminder");
		return "reminder-create";
	}

	@RequestMapping("/addReminder")
	public String addReminder(Model model, Reminder reminder, RedirectAttributes redirectAttributes) {

		reminderService.saveReminderToDB(reminder);
		redirectAttributes.addFlashAttribute("successMessage", "Reminder added successfully!");
		return "redirect:/reminder";

	}

	@RequestMapping(value = "/editReminder",
			method = RequestMethod.GET)
	public String editReminder(Model model, @RequestParam("id") Long id) throws Exception{

		System.out.println("Got edit request for reminder with id " + id);

		model.addAttribute("header", "Edit Reminder");
		model.addAttribute("reminder", reminderService.findReminderById(id));

		return "reminder-create";

	}

	@RequestMapping(value = "/viewReminder",
			method = RequestMethod.GET)
	public String viewReminder(Model model, @RequestParam("id") String id) throws Exception{

		System.out.println("Got view request for reminder id " + id);
		model.addAttribute("reminder", reminderService.findReminderById(Long.parseLong(id)));
		return "view-reminder";

	}

	@RequestMapping(value = "/deleteReminder",
			method = RequestMethod.GET)
	public String deleteReminder(RedirectAttributes redirectAttributes, @RequestParam("id") Long id) throws IOException {

		System.out.println("Got delete request for reminder id " + id);
		reminderService.deleteReminderById(id);
		redirectAttributes.addFlashAttribute("successMessage", "Reminder record deleted successfully!");
		return "redirect:/reminder";

	}
	
	@Scheduled(cron = "0 30 9 * * *")
//	@Scheduled(fixedRate = 5000)
	public void scheduleTask(){
		String strDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS").format(new Date());
		System.out.println("Reminder scheduler: Job running at - " + strDate);
		
		List<Reminder> listOfReminders = reminderService.getAllPendingRemindersForToday();
		
		Reminder tempReminder = null;
		for (Reminder reminder : listOfReminders) {
			try {
				if("Success".equals(utility.sendSMS(reminder.getPhone(), reminder.getMessage()))) {
					tempReminder = reminderService.findReminderById(reminder.getId());
					tempReminder.setStatus("Sent");
					reminderService.saveReminderToDB(tempReminder);
					System.out.println("Reminder sent to - " + reminder.getName() + " in number - " + reminder.getPhone());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}

}
