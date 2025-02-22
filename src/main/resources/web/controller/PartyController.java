package com.hunter.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hunter.web.model.Party;
import com.hunter.web.service.PartyService;

@Controller
public class PartyController {

	@Autowired private PartyService partyService;

	@GetMapping("/party")
	public String showPartyPage(Model model) {
		model.addAttribute("partyList", partyService.getAllUsers());
		return "party";
	}

	@GetMapping("/add-party-page")
	public String showAddPartyPage(Model model) {
		model.addAttribute("party", new Party());
		model.addAttribute("header", "Create Party");
		return "party-create";
	}

	@RequestMapping(value = "/create-party",
			method = RequestMethod.POST)
	public String createParty(Model model, Party party, 
			RedirectAttributes redirectAttributes) throws Exception{

		party = partyService.saveUserToDB(party);
		redirectAttributes.addFlashAttribute("successMessage", "New user " + party.getName() + " added successfully as Party!");
		return "redirect:/party";

	}

	@RequestMapping(value = "/viewParty",
			method = RequestMethod.GET)
	public String viewParty(Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got view request for party id " + id);
		model.addAttribute("party", partyService.findUserById(Long.parseLong(id)));
		return "view-party";

	}

	@RequestMapping(value = "/editParty",
			method = RequestMethod.GET)
	public String editParty(Model model,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got edit request for party id " + id);
		model.addAttribute("party", partyService.findUserById(Long.parseLong(id)));
		model.addAttribute("header", "Edit Party");
		return "party-create";

	}

	@RequestMapping(value = "/deleteParty",
			method = RequestMethod.GET)
	public String deleteParty(RedirectAttributes redirectAttributes,
			@RequestParam("action") String action,
			@RequestParam("id") String id) throws Exception{

		System.out.println("Got delete request for party id " + id);
		Party party = partyService.findUserById(Long.parseLong(id));
		
		if(party != null) {
			if(party.getStockInList().isEmpty()) {
				partyService.deleteUserById(Long.parseLong(id));
				redirectAttributes.addFlashAttribute("successMessage", "Party with id " + id + " deleted successfully!");
			} else {
				redirectAttributes.addFlashAttribute("successMessage", party.getStockInList().size() + " Stock In entries present this party!");
			}
		}
		return "redirect:/party";
		
	}

}
