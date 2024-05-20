package rcn.web.controller;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import rcn.web.model.Bill;
import rcn.web.model.Connection;
import rcn.web.model.Consumer;
import rcn.web.service.BillService;
import rcn.web.service.ConnectionService;
import rcn.web.service.ConsumerService;

@Controller
@RequestMapping("/bill")
public class BillController {
	
	@Autowired BillService billService;
	@Autowired ConnectionService connectionService;
	@Autowired ConsumerService consumerService;
	
	@Value("${INITIAL_PAGE_SIZE}")
	private Integer initialPageSize;

	@GetMapping
	public String bill(Model model,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			@RequestParam(value="fromDate", required = false) String fromDate,
			@RequestParam(value="toDate", required = false) String toDate,
			@RequestParam(value="keyword", required = false) String keyword,
			@RequestParam(value="connectionId", required = false) Long connectionId) throws ParseException {
		
		Page<Bill> listPage = null;
		
		if(keyword == null && fromDate == null && toDate == null) {
			System.out.println("Bill home page");
			if(connectionId != null) listPage = billService.getPageByConnectionId(connectionId, page.orElse(1) - 1, size.orElse(initialPageSize));
			else listPage = billService.getAll(page.orElse(1) - 1, size.orElse(initialPageSize));
			
		} else {
			System.out.println("Searching Bill for fromDate:" + fromDate + " and toDate:" +toDate +" and consumer name:" + keyword);
			listPage = billService.getPageByDateAndConsumer(fromDate, toDate, connectionId, page.orElse(1) - 1, size.orElse(initialPageSize));
			
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

		return "app/bill";

	}
	
	@RequestMapping("/bill/record")
	public String billRecord(Model model) {
//		model.addAttribute("billRecordList", billService.getBillRecord());
		return "bill-record";
	}
	
	@Scheduled(cron = "0 30 9 * * *")
//	@Scheduled(fixedRate = 5000)
	public void scheduleTask(){
		String strDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS").format(new Date());
		System.out.println("Reminder scheduler: Job running at - " + strDate);
		
		List<Consumer> listOfConsumers = consumerService.getAll();
		System.out.println("Calculatng bill for consumer size - " + listOfConsumers.size());
		for (Consumer consumer : listOfConsumers) {
			for(Connection connection : consumer.getConnections()) {
				//generate new bill
			}
		}
	}
	
}
