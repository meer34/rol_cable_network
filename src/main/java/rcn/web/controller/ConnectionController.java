package rcn.web.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import rcn.web.model.Bill;
import rcn.web.model.Connection;
import rcn.web.model.Due;
import rcn.web.service.BillService;
import rcn.web.service.CollectionService;
import rcn.web.service.ConnectionService;
import rcn.web.service.ConsumerService;
import rcn.web.service.DueService;
import rcn.web.service.SubscriptionService;
import rcn.web.util.AppUtility;

@Controller
@RequestMapping("/connection")
public class ConnectionController {

	@Value("${INITIAL_PAGE_SIZE}")
	private Integer initialPageSize;
	
	@Value("${RENEWAL_CYCLE}")
	private int renewalCycle;
	
	@Autowired ConnectionService connectionService;
	@Autowired ConsumerService consumerService;
	@Autowired SubscriptionService subscriptionService;
	@Autowired BillService billService;
	@Autowired CollectionService collectionService;
	@Autowired DueService dueService;
	@Autowired AppUtility utility;

	@GetMapping
	public String showBasePage(Model model,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			@RequestParam(value="fromDate", required = false) String fromDate,
			@RequestParam(value="toDate", required = false) String toDate,
			@RequestParam(value="keyword", required = false) String keyword,
			@RequestParam(value="consumerId", required = false) Long consumerId) {

		Page<Connection> listPage = null;

		if(keyword == null && fromDate == null && toDate == null) {
			System.out.println("Connection home page");
			if(consumerId != null) listPage = connectionService.getPageByConsumerId(consumerId, page.orElse(1) - 1, size.orElse(initialPageSize));
			else listPage = connectionService.getAll(page.orElse(1) - 1, size.orElse(initialPageSize));

		} else {
			System.out.println("Searching Connection for fromDate:" + fromDate + " and toDate:" +toDate +" and keyword:" + keyword);
			listPage = connectionService.searchByDateAndKeyword(keyword, fromDate, toDate, page.orElse(1) - 1, size.orElse(initialPageSize));

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

		return "app/connection";

	}

	@GetMapping("/add")
	public String add(Model model, Connection connection) {
		model.addAttribute("header", "Create Connection");
		model.addAttribute("consumerList", consumerService.getAll());
		model.addAttribute("packageList", subscriptionService.getAllPackages());
		model.addAttribute("bucketList", subscriptionService.getAllBuckets());
		model.addAttribute("channelList", subscriptionService.getAllChannels());
		model.addAttribute("renewalCycle", renewalCycle);
		return "app/connection-create";
	}

	@RequestMapping(value = "/save",
			method = RequestMethod.POST)
	public String save(Model model, Connection connection, RedirectAttributes redirectAttributes,
			@RequestParam(value="stateChangeDate", required=false) String stateChangeDate) throws Exception{
		
		if(connection.getId() == null) {
			connection.setState("Connected");
			
			connection = connectionService.save(connection);
			generateBill(connection);
			
			if(connection.getConnectionCharge() > 0) {
				Due connChargeDue = new Due();
				connChargeDue.setDueType("Connection Charge");
				connChargeDue.setDueAmount(connection.getConnectionCharge());
//				connChargeDue.setDateOfDueEntry(utility.getTodaysDateWithoutTime()); //Got 17th date in VPS on 18th Jan
				connChargeDue.setDateOfDueEntry(connection.getDate());
				connChargeDue.setRemarks("Auto entry for fresh connection");
				connChargeDue.setConsumer(connection.getConsumer());

				dueService.saveDue(connChargeDue);
			}
			
			if(connection.getPreviousDue() > 0) {
				Due prevDue = new Due();
				prevDue.setDueType("Previous Due");
				prevDue.setDueAmount(connection.getPreviousDue());
				prevDue.setDateOfDueEntry(connection.getDate());
				prevDue.setRemarks("Auto entry for fresh connection");
				prevDue.setConsumer(connection.getConsumer());

				dueService.saveDue(prevDue);
			}
			
		} else {
			Connection savedConn = connectionService.getById(connection.getId());
			
			if(connection.getState().equals("Disconnected") & !savedConn.getState().equals("Disconnected")) {
				Date disconnectedDate = null;

				if("Today".equals(stateChangeDate)) {
					disconnectedDate = utility.getTodaysDateWithoutTime();
				} else {
					disconnectedDate = utility.formatStringToDate(stateChangeDate);
				}
				generateBillForPeriod(connection, connection.getSubscriptionAmount(), connection.getDateOfConnStart(), disconnectedDate);
				
				Bill oldBill = billService.getBillForPeriod(connection, connection.getDateOfConnStart(), connection.getDateOfConnExpiry());
				collectionService.removeBillFromCollections(oldBill);
				billService.deleteById(oldBill.getId());
				
				double paidAmountReturn = oldBill.getPaidAmount();
				connection.setAdvanceAmount(connection.getAdvanceAmount() + paidAmountReturn);
				connection.setDateOfConnExpiry(disconnectedDate);

			} else if(!connection.getState().equals("Disconnected") & savedConn.getState().equals("Disconnected")) {
				Date reConnectedDate = null;

				if("Today".equals(stateChangeDate)) reConnectedDate = utility.getTodaysDateWithoutTime();
				else reConnectedDate = utility.formatStringToDate(stateChangeDate);

				connection.setDateOfConnStart(reConnectedDate);

				Calendar c = Calendar.getInstance();
				c.setTime(connection.getDateOfConnStart());
				c.add(Calendar.DATE, renewalCycle - 1);

				connection.setDateOfConnExpiry(c.getTime());
				
				generateBill(connection);
				
			} else if(connection.getSubscriptionAmount() != savedConn.getSubscriptionAmount()){
				double subscriptionAnountDifference = connection.getSubscriptionAmount() - savedConn.getSubscriptionAmount();
				
				if(subscriptionAnountDifference > 0)
					generateBillForPeriod(connection, subscriptionAnountDifference, utility.getTodaysDateWithoutTime(), connection.getDateOfConnExpiry());
				else if(subscriptionAnountDifference < 0)
					generateBillForPeriod(connection, subscriptionAnountDifference, utility.getTomorrowsDateWithoutTime(), connection.getDateOfConnExpiry());
			}
			
			connection = connectionService.save(connection);
		}

		redirectAttributes.addFlashAttribute("successMessage", "Connection for " + connection.getConsumer().getFullName() + " saved successfully!");
		return "redirect:/connection";

	}

	private void generateBillForPeriod(Connection connection, double subscriptionAmount, Date billStartDate, Date billEndDate) {
		System.out.println("Generating bill for disconnected connection id " + connection.getId());
		
		Bill bill = new Bill();
		bill.setConnection(connection);
		bill.setStartDate(billStartDate);
		bill.setEndDate(billEndDate);
		bill.setBillAmount(subscriptionAmount*utility.getDifferenceDays(billStartDate, billEndDate)/30);
		bill.setPaidAmount(0.0);
		
		billService.save(bill);
		System.out.println("Bill with id: " + bill.getId() + " generated for consumer: " 
									+ connection.getConsumer().getFullName());
	}

	@RequestMapping(value = "/view",
			method = RequestMethod.GET)
	public String view(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id,
			@RequestParam(value="consumerId", required = false) String consumerId) throws Exception{

		System.out.println("Got view request for connection id " + id);
		if(consumerId != null) {
			model.addAttribute("connection", connectionService.getByConsumerId(Long.parseLong(consumerId)));
		} else {
			model.addAttribute("connection", connectionService.getById(Long.parseLong(id)));
		}
		model.addAttribute("consumerList", consumerService.getAll());
		model.addAttribute("packageList", subscriptionService.getAllPackages());
		model.addAttribute("bucketList", subscriptionService.getAllBuckets());
		model.addAttribute("channelList", subscriptionService.getAllChannels());
		model.addAttribute("header", "View Connection");
		
		return "app/connection-view";
	}

	@RequestMapping(value = "/edit",
			method = RequestMethod.GET)
	public String edit(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="id", required = false) String id,
			@RequestParam(value="consumerId", required = false) String consumerId) throws Exception{

		System.out.println("Got edit request for connection id " + id);
		if(consumerId != null) {
			model.addAttribute("connection", connectionService.getByConsumerId(Long.parseLong(consumerId)));
		} else {
			model.addAttribute("connection", connectionService.getById(Long.parseLong(id)));
		}
		model.addAttribute("header", "Edit Connection");
		model.addAttribute("consumerList", consumerService.getAll());
		model.addAttribute("packageList", subscriptionService.getAllPackages());
		model.addAttribute("bucketList", subscriptionService.getAllBuckets());
		model.addAttribute("channelList", subscriptionService.getAllChannels());
		model.addAttribute("renewalCycle", renewalCycle);
		
		return "app/connection-create";
	}

	@RequestMapping(value = "/delete",
			method = RequestMethod.GET)
	public String delete(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got delete request for connection id " + id);
		
		List<Bill> listOfBills = connectionService.getById(Long.parseLong(id)).getBills();
		if(listOfBills != null & listOfBills.size() >0) {
			redirectAttributes.addFlashAttribute("errorMessage", "Connection with id " + id + " cannot be deleted since it has bills associated with it!");
		} else {
			connectionService.deleteById(Long.parseLong(id));
			redirectAttributes.addFlashAttribute("successMessage", "Connection with id " + id + " deleted successfully!");
		}
		
		return "redirect:/connection";
	}
	
	@RequestMapping(value = "/renew",
			method = RequestMethod.GET)
	public String renew(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("id") String id,
			@RequestParam("date") String date) throws Exception{
		
		System.out.println("Got renew request for connection id - " + id + ", renewal date - " + date);
		Connection connection = connectionService.getById(Long.parseLong(id));
		
		connection.setDateOfConnStart( utility.formatStringToDate(date));
		connection.setDateOfConnExpiry(utility.getOneMonthAheadDate(connection.getDateOfConnStart()));
		
		generateBill(connection);
		
		connection.setState("Connected");
		
		connectionService.save(connection);
		
		redirectAttributes.addFlashAttribute("successMessage", "Connection for " + connection.getConsumer().getFullName() + " renewed successfully!");
		return "redirect:/connection";

	}
	
	private void generateBill(Connection connection) {
		System.out.println("Generating bill for connection id " + connection.getId());
		
		Bill bill = new Bill();
		bill.setConnection(connection);
		bill.setStartDate(connection.getDateOfConnStart());
		bill.setEndDate(connection.getDateOfConnExpiry());
		bill.setBillAmount(connection.getSubscriptionAmount());
		bill.setPaidAmount(0.0);
		
		billService.save(bill);
		System.out.println("Bill with id: " + bill.getId() + " generated for consumer: " 
									+ connection.getConsumer().getFullName());
	}

}
