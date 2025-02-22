package rcn.web.controller;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import rcn.web.model.Consumer;
import rcn.web.model.Reminder;
import rcn.web.service.ConsumerService;
import rcn.web.service.ReminderService;
import rcn.web.util.AppUtility;

@Controller
@PropertySource("classpath:rol_cable_network.properties")
public class ReminderController {

	@Autowired ReminderService reminderService;
	@Autowired ConsumerService consumerService;
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
	
	@RequestMapping("/add-reminder-queue")
	@PreAuthorize("hasAuthority('ADMIN')")
	public String addReminderQueue(RedirectAttributes redirectAttributes, @RequestParam("consumerIds") Long[] consumerIds) {

		Reminder reminder = null;
		
		for (Long consumerId : consumerIds) {
			try {
				Consumer consumer = consumerService.getById(consumerId);
				
		        String fullName = consumer.getFullName();
		        double totalPending = consumer.getTotalPending();

		        // Format the amount with a thousands separator
		        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
		        currencyFormat.setMinimumFractionDigits(2);
		        currencyFormat.setMaximumFractionDigits(2);
		        String formattedAmount = currencyFormat.format(totalPending);

		        // Create the message
		        String message = String.format(
		            "Dear %s,\nYou have a pending due of ₹%s. Kindly pay at your earliest to avoid service disruption.\n– Rol Cable Network",
		            fullName,
		            formattedAmount
		        );

		        // URL-encode the message for sending via Textlocal API
		        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);

				reminder = new Reminder();
				reminder.setPhone(consumer.getPhoneNo());
				reminder.setMessage(message);
				reminder.setName(consumer.getFullName());
				reminder.setStatus("Pending");
				reminder.setDate(utility.getTodaysDateWithoutTime());

				reminderService.saveReminderToDB(reminder);

			} catch (Exception e) {
				System.out.println("Failed to add reminder for consumerId: " + consumerId);
			}
		}
		redirectAttributes.addFlashAttribute("successMessage", "Reminders added successfully!");
		return "redirect:/consumer/filter";

	}

	@RequestMapping("/addNewReminderPage")
	@PreAuthorize("hasAnyAuthority('ADMIN','ADD_REMINDER')")
	public String addNewReminderPage(Model model) {
		model.addAttribute("header", "Add Reminder");
		return "reminder-create";
	}

	@RequestMapping("/addReminder")
	@PreAuthorize("hasAnyAuthority('ADMIN','ADD_REMINDER')")
	public String addReminder(Model model, Reminder reminder, RedirectAttributes redirectAttributes) {

		reminderService.saveReminderToDB(reminder);
		redirectAttributes.addFlashAttribute("successMessage", "Reminder added successfully!");
		return "redirect:/reminder";

	}

	@RequestMapping(value = "/editReminder",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
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
	@PreAuthorize("hasAuthority('ADMIN')")
	public String deleteReminder(RedirectAttributes redirectAttributes, @RequestParam("id") Long id) throws IOException {

		System.out.println("Got delete request for reminder id " + id);
		reminderService.deleteReminderById(id);
		redirectAttributes.addFlashAttribute("successMessage", "Reminder record deleted successfully!");
		return "redirect:/reminder";

	}
	
//	@Scheduled(cron = "0 30 9 * * *")
//	@Scheduled(fixedRate = 5000)
	public void scheduleTask(){
		String strDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
		System.out.println("Reminder scheduler: Job running at - " + strDate);
		
		List<Reminder> listOfReminders = reminderService.getAllPendingRemindersForToday();
		System.out.println("Size is - " + listOfReminders.size());
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
