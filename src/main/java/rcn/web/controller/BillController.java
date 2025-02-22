package rcn.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import rcn.web.model.Bill;
import rcn.web.model.Connection;
import rcn.web.model.Consumer;
import rcn.web.model.Due;
import rcn.web.service.BillService;
import rcn.web.service.ConnectionService;
import rcn.web.service.ConsumerService;
import rcn.web.util.AppUtility;

@Controller
@RequestMapping("/bill")
public class BillController {

	@Autowired BillService billService;
	@Autowired ConnectionService connectionService;
	@Autowired ConsumerService consumerService;
	@Autowired AppUtility utility;

	@Value("${INITIAL_PAGE_SIZE}")
	private Integer initialPageSize;

	@GetMapping
	public String bill(Model model,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			@RequestParam(value="year", required = false) String year,
			@RequestParam(value="consumerId", required = false) String consumerId) throws ParseException {

		Page<Consumer> listPage = null;

		System.out.println("Bill home page");
		if(year == null || year.equals("All Years")) {
			listPage = consumerService.getAll(page.orElse(1) - 1, size.orElse(initialPageSize));
			for (Consumer consumer : listPage) {
				consumer.calculateAllBillAndTotalPaid();
			}
		} else {
			String yearStart ="01/01/" + year;
			String yearEnd ="31/12/" + year;
			Date yearStartDate=new SimpleDateFormat("dd/MM/yyyy").parse(yearStart);
			Date yearEndDate=new SimpleDateFormat("dd/MM/yyyy").parse(yearEnd);
			listPage = consumerService.getAll(page.orElse(1) - 1, size.orElse(initialPageSize));
			for (Consumer consumer : listPage) {
				consumer.calculateTotalSubscriptionBillForYear(yearStartDate, yearEndDate);
				consumer.calculateTotalOtherDueBillForYear(yearStartDate, yearEndDate);
				consumer.calculateTotalPaidForYear(yearStartDate, yearEndDate);
			}
		}

		model.addAttribute("year", year);
		if(year != null) model.addAttribute("successMessage", "Presenting calculations for year " + year + " only.");
		
		model.addAttribute("listPage", listPage);
		int totalPages = listPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
					.boxed()
					.collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		return "app/bill";

	}
	
	@RequestMapping(value = "/save",
			method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String save(Model model, Bill bill, RedirectAttributes redirectAttributes) throws Exception{
		bill = billService.save(bill);
		redirectAttributes.addFlashAttribute("successMessage", "Bill amount of " + bill.getBillAmount() 
		+ " saved successfully for consumer: " + bill.getConnection().getConsumer().getFullName());
		redirectAttributes.addAttribute("consumerId", bill.getConnection().getConsumer().getId());
		return "redirect:/bill/getSubscriptionBillRecordsForConsumer";

	}
	
	@RequestMapping(value = "/edit",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String edit(RedirectAttributes redirectAttributes, Model model,
			@RequestParam(value="billId", required = false) String billId) throws Exception{

		System.out.println("Got edit request for dueId " + billId);
		Bill bill = billService.getById(Long.parseLong(billId));
		model.addAttribute("bill", bill);
		model.addAttribute("connectionId", bill.getConnection().getId());
		model.addAttribute("header", "Edit Bill");
		return "app/bill-edit";
	}

	@RequestMapping(value = "/delete",
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String delete(RedirectAttributes redirectAttributes, Model model,
			@RequestParam("billId") String billId) throws Exception{

		System.out.println("Got delete request for billId: " + billId);
		Long consumerId = billService.getById(Long.parseLong(billId))
				.getConnection()
				.getConsumer()
				.getId();
		redirectAttributes.addAttribute("consumerId", consumerId);
		
		Bill bill = billService.getById(Long.parseLong(billId));
		
		if(bill.getPaidAmount() == 0.0) {
			billService.deleteById(Long.parseLong(billId));
			redirectAttributes.addFlashAttribute("successMessage", "Bill with id " + billId + " deleted successfully for consumer id " + consumerId);
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete Bill since it already has paid amount!");
		}
		
		return "redirect:/bill/getSubscriptionBillRecordsForConsumer";
	}

	@GetMapping("/getBillForConsumer")
	public String getBillByConsumerId(Model model,
			@RequestParam(value="consumerId", required = true) Long consumerId) throws ParseException {

		List<Consumer> listOfConsumers = new ArrayList<>();
		Consumer consumer = null;

		System.out.println("Bill home page");
		if(consumerId != null) {
			consumer = consumerService.getById(consumerId);
			consumer.calculateTotalSubscriptionBill();
			consumer.calculateTotalOtherDueBill();
			consumer.calculateTotalPaid();
			listOfConsumers.add(consumer);
		}

		Page<Consumer> listPage = new PageImpl<>(listOfConsumers);

		model.addAttribute("listPage", listPage);
		int totalPages = listPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
					.boxed()
					.collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		return "app/bill";

	}

	@GetMapping("/getSubscriptionBillRecordsForConsumer")
	public String getSubscriptionBillsByConsumerId(Model model,
			@RequestParam(value="consumerId", required = true) Long consumerId,
			@RequestParam(value="showAll", required = false) boolean showAll) throws ParseException {

		System.out.println("Get Subscription Bill Page for consumerId: " + consumerId);

		List<Bill> listOfBills = new ArrayList<>();
		Consumer consumer = consumerService.getById(consumerId);

		for (Connection connection : consumer.getConnections()) {
			List<Bill> bills = connection.getBills();
			if(!showAll) {
				bills = bills.stream()
						.filter(bill -> bill.getBillAmount() - bill.getPaidAmount() > 0)
						.collect(Collectors.toList());
			}
			listOfBills.addAll(bills);
		}

		model.addAttribute("listOfBills", listOfBills);
		model.addAttribute("consumerId", consumerId);
		return "app/bill-record";

	}

	@GetMapping("/getDueRecordsForConsumer")
	public String getDuesByConsumerId(Model model,
			@RequestParam(value="consumerId", required = true) Long consumerId,
			@RequestParam(value="showAll", required = false) boolean showAll) throws ParseException {

		System.out.println("Get due records page for consumerId: " + consumerId);

		Consumer consumer = consumerService.getById(consumerId);
		List<Due> listOfDues = consumer.getDues(); 
		
		if(!showAll) {
			listOfDues = listOfDues.stream()
					.filter(due -> due.getDueAmount() - due.getPaidAmount() > 0)
					.collect(Collectors.toList());
		}

		model.addAttribute("listOfDues", listOfDues);
		
		model.addAttribute("consumerId", consumerId);
		return "app/due-record";

	}


//	@Scheduled(cron = "0 30 9 * * *")
//	@Scheduled(fixedRate = 30000)
	public void autoRenewTask() throws ParseException{
//		String strDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
//		System.out.println("Bill scheduler: Job running at - " + strDate);

		List<Consumer> listOfConsumers = consumerService.getAll();
//		System.out.println("Calculatng bill for consumer size - " + listOfConsumers.size());
		for (Consumer consumer : listOfConsumers) {
			List<Connection> listOfConnection = connectionService.getByConsumerId(consumer.getId());
			for(Connection connection : listOfConnection) {
				//generate new bill if auto renew able and connection expired
				if(connection.isAutoRenewal() && !"Disconnected".equals(connection.getState())) {
					if(connection.getDateOfConnExpiry().before(utility.getTodaysDateWithoutTime())) {
						System.out.println("Connection Expired and disconnected for consumer: " 
								+ connection.getConsumer().getFullName());
						renewConnectionAndGenerateBill(connection);
					}
				};
			}
		}
	}

	public void renewConnectionAndGenerateBill(Connection connection) {
		System.out.println("Generating bill for connection id " + connection.getId());
		
		connection.setDateOfConnStart(connection.getDateOfConnExpiry());
		connection.setDateOfConnExpiry(utility.getOneMonthAheadDate(connection.getDateOfConnStart()));
		
		Bill bill = new Bill();
		bill.setConnection(connection);
		bill.setStartDate(connection.getDateOfConnStart());
		bill.setEndDate(connection.getDateOfConnExpiry());
		bill.setBillAmount(connection.getSubscriptionAmount());
		bill.setPaidAmount(0.0);
		
		billService.save(bill);
		connectionService.save(connection);
		System.out.println("Bill with id: " + bill.getId() + " generated for consumer: " 
									+ connection.getConsumer().getFullName());
//		billService.save(bill);//TODOO
	}

}
