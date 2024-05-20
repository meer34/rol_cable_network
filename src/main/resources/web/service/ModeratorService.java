package com.hunter.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hunter.web.model.Moderator;
import com.hunter.web.model.User;
import com.hunter.web.repo.ModeratorRepo;
import com.hunter.web.repo.UserRepo;

@Service
public class ModeratorService {

	@Autowired private ModeratorRepo moderatorRepo;
	@Autowired private UserRepo userRepo;

	public Moderator saveUserToDB(Moderator moderator) {
		moderator.setSynced(false);
		return moderatorRepo.save(moderator);
	}
	
	public Moderator findUserById(Long id) {
		return moderatorRepo.findById(id).get();
	}

	public List<Moderator> getAllUsers() {
		return moderatorRepo.findAll();
	}

	public void deleteUserById(Long id) {
		moderatorRepo.deleteById(id);
	}

	public List<Moderator> getModeratorsByPhoneNumberAndIdNotMatching(String phone, Long id) {
		return moderatorRepo.findByPhoneAndIdNot(phone, id);
	}
	
	public List<Moderator> findAllNotSyncedData() {
		return moderatorRepo.findAllNotSyncedData();
	}
	
	public Moderator saveRemoteData(Moderator moderator) {
		moderator.setSynced(true);
		
		Long tempModeratorId = moderator.getId();
		if(moderator.getRemoteId() != null) moderator.setId(moderator.getRemoteId());
		else moderator.setId(0L);
		moderator.setRemoteId(tempModeratorId);
		
		User tempUser = userRepo.getUserByPhoneNumber(moderator.getUser().getPhone());
		if(tempUser != null) {
			moderator.getUser().setId(tempUser.getId());
			moderator.getUser().setRoles(tempUser.getRoles());
		}
		
		System.out.println("Saving Moderator with id: " + moderator.getId() + " remote id: " + moderator.getRemoteId());
		return moderatorRepo.saveAndFlush(moderator);
	}

	public void markAsSynced(Long id, Long serverId) {
		moderatorRepo.markAsSynced(id, serverId);
	}

}
