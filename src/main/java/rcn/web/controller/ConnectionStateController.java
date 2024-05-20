package rcn.web.controller;

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
import org.springframework.web.bind.annotation.RequestParam;

import rcn.web.model.Consumer;
import rcn.web.service.BillService;
import rcn.web.service.ConnectionService;
import rcn.web.service.ConsumerService;
import rcn.web.service.SubscriptionService;




@Controller
@RequestMapping({"/connectionState"})
public class ConnectionStateController
{
  @Value("${INITIAL_PAGE_SIZE}")
  private Integer initialPageSize;
  @Autowired
  ConnectionService connectionService;
  @Autowired
  ConsumerService consumerService;
  @Autowired
  SubscriptionService subscriptionService;
  @Autowired
  BillService billService;
  
  @GetMapping
  public String showBasePage(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, @RequestParam(value = "fromDate", required = false) String fromDate, @RequestParam(value = "toDate", required = false) String toDate, @RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "consumerId", required = false) Long consumerId) {
    List<Consumer> consumerList = this.consumerService.getAll(); 
    
    model.addAttribute("consumerList", consumerList);
    
    return "app/connection-state";
  }

}
