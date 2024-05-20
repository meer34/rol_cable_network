package com.hunter.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hunter.web.model.Party;
import com.hunter.web.repo.PartyRepo;

@Service
public class PartyService {

	@Autowired
	private PartyRepo partyRepo;

	public Party saveUserToDB(Party party) {
		party.setSynced(false);
		return partyRepo.save(party);
	}
	
	public Party findUserById(Long id) {
		return partyRepo.findById(id).get();
	}

	public List<Party> getAllUsers() {
		return partyRepo.findAll();
	}

	public void deleteUserById(Long id) {
		partyRepo.deleteById(id);
	}
	
	public List<Party> findAllNotSyncedData() {
		return partyRepo.findAllNotSyncedData();
	}

	public Party saveRemoteData(Party party) {
		party.setSynced(true);
		
		Long tempPartyId = party.getId();
		if(party.getRemoteId() != null) party.setId(party.getRemoteId());
		else party.setId(0L);
		party.setRemoteId(tempPartyId);
		
		System.out.println("Saving party with id: " + party.getId() + " remote id: " + party.getRemoteId());
		return partyRepo.saveAndFlush(party);
	}

	public void markAsSynced(Long id, Long serverId) {
		partyRepo.markAsSynced(id, serverId);
	}

}
