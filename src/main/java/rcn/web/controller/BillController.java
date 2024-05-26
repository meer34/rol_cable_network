package rcn.web.controller;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
				consumer.calculateTotalSubscriptionBill();
				consumer.calculateTotalOtherDueBill();
				consumer.calculateTotalPaid();
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
			@RequestParam(value="consumerId", required = true) Long consumerId) throws ParseException {

		System.out.println("Subscription Bill Home Page for consumerId: " + consumerId);

		List<Bill> listOfBills = new ArrayList<>();
		Consumer consumer = consumerService.getById(consumerId);

		for (Connection connection : consumer.getConnections()) {
			for (Bill bill : connection.getBills()) {
				listOfBills.add(bill);
			}
		}

		Page<Bill> listPage = new PageImpl<>(listOfBills);

		model.addAttribute("listPage", listPage);
		int totalPages = listPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
					.boxed()
					.collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		return "app/bill-record";

	}

	@GetMapping("/getDueRecordsForConsumer")
	public String getDuesByConsumerId(Model model,
			@RequestParam(value="consumerId", required = true) Long consumerId) throws ParseException {

		System.out.println("Subscription Bill Home Page for consumerId: " + consumerId);

		List<Due> listOfDues = new ArrayList<>();
		Consumer consumer = consumerService.getById(consumerId);

		for (Due due : consumer.getDues()) {
			listOfDues.add(due);
		}

		Page<Due> listPage = new PageImpl<>(listOfDues);

		model.addAttribute("listPage", listPage);
		int totalPages = listPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
					.boxed()
					.collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		return "app/due-record";

	}


//	@Scheduled(cron = "0 30 9 * * *")
//		@Scheduled(fixedRate = 30000)
	public void scheduleTask(){
		String strDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS").format(new Date());
		System.out.println("Reminder scheduler: Job running at - " + strDate);

		List<Consumer> listOfConsumers = consumerService.getAll();
		System.out.println("Calculatng bill for consumer size - " + listOfConsumers.size());
		for (Consumer consumer : listOfConsumers) {
			List<Connection> listOfConnection = connectionService.getByConsumerId(consumer.getId());
			for(Connection connection : listOfConnection) {
				//generate new bill if connection expired
				if(!"Disconnected".equals(connection.getState())) {
					if(connection.getDateOfConnExpiry().before(utility.getTodaysDateWithoutTime())) {
						System.out.println("Connection Expired and disconnected for consumer: " 
										+ connection.getConsumer().getFullName());
						generateBill(connection);
				}
				};
			}
		}
	}

	public void generateBill(Connection connection) {
		System.out.println("Generating bill for connection id " + connection.getId());
		
		connection.setDateOfConnStart(connection.getDateOfConnExpiry());
		
		Calendar c = Calendar.getInstance();
        c.setTime(connection.getDateOfConnStart());
        c.add(Calendar.DATE, 30);
        
		connection.setDateOfConnExpiry(c.getTime());
		
		
		
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
		billService.save(bill);
	}

}
