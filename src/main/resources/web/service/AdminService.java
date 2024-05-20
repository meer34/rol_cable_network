package com.hunter.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hunter.web.model.Admin;
import com.hunter.web.model.User;
import com.hunter.web.repo.AdminRepo;
import com.hunter.web.repo.UserRepo;

@Service
public class AdminService {

	@Autowired private AdminRepo adminRepo;
	@Autowired private UserRepo userRepo;

	public Admin saveUserToDB(Admin admin) {
		admin.setSynced(false);
		return adminRepo.save(admin);
	}
	
	public Admin findUserById(Long id) {
		return adminRepo.findById(id).get();
	}

	public List<Admin> getAllUsers() {
		return adminRepo.findAll();
	}

	public void deleteUserById(Long id) {
		adminRepo.deleteById(id);
	}

	public List<Admin> getAdminsByPhoneNumberAndIdNotMatching(String phone, Long id) {
		return adminRepo.findByPhoneAndIdNot(phone, id);
	}
	
	public List<Admin> findAllNotSyncedData() {
		return adminRepo.findAllNotSyncedData();
	}

	public Admin saveRemoteData(Admin admin) {
		admin.setSynced(true);
		
		Long tempAdminId = admin.getId();
		if(admin.getRemoteId() != null) admin.setId(admin.getRemoteId());
		else admin.setId(0L);
		admin.setRemoteId(tempAdminId);
		
		User tempUser = userRepo.getUserByPhoneNumber(admin.getUser().getPhone());
		if(tempUser != null) {
			admin.getUser().setId(tempUser.getId());
			admin.getUser().setRoles(tempUser.getRoles());
		}
		
		System.out.println("Saving Admin with id: " + admin.getId() + " remote id: " + admin.getRemoteId());
		return adminRepo.saveAndFlush(admin);
	}

	public void markAsSynced(Long id, Long serverId) {
		adminRepo.markAsSynced(id, serverId);
	}

}
