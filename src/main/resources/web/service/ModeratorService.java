package com.hunter.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hunter.web.model.App User;
import com.hunter.web.model.User;
import com.hunter.web.repo.App UserRepo;
import com.hunter.web.repo.UserRepo;

@Service
public class appUserService {

	@Autowired private App UserRepo appUserRepo;
	@Autowired private UserRepo userRepo;

	public App User saveUserToDB(App User appUser) {
		appUser.setSynced(false);
		return appUserRepo.save(appUser);
	}
	
	public App User findUserById(Long id) {
		return appUserRepo.findById(id).get();
	}

	public List<App User> getAllUsers() {
		return appUserRepo.findAll();
	}

	public void deleteUserById(Long id) {
		appUserRepo.deleteById(id);
	}

	public List<App User> getApp UsersByPhoneNumberAndIdNotMatching(String phone, Long id) {
		return appUserRepo.findByPhoneAndIdNot(phone, id);
	}
	
	public List<App User> findAllNotSyncedData() {
		return appUserRepo.findAllNotSyncedData();
	}
	
	public App User saveRemoteData(App User appUser) {
		appUser.setSynced(true);
		
		Long tempAppUserId = appUser.getId();
		if(appUser.getRemoteId() != null) appUser.setId(appUser.getRemoteId());
		else appUser.setId(0L);
		appUser.setRemoteId(tempAppUserId);
		
		User tempUser = userRepo.getUserByPhoneNumber(appUser.getUser().getPhone());
		if(tempUser != null) {
			appUser.getUser().setId(tempUser.getId());
			appUser.getUser().setRoles(tempUser.getRoles());
		}
		
		System.out.println("Saving App User with id: " + appUser.getId() + " remote id: " + appUser.getRemoteId());
		return appUserRepo.saveAndFlush(appUser);
	}

	public void markAsSynced(Long id, Long serverId) {
		appUserRepo.markAsSynced(id, serverId);
	}

}
