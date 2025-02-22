package com.hunter.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hunter.web.model.Customer;
import com.hunter.web.repo.CustomerRepo;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepo customerRepo;

	public Customer saveUserToDB(Customer admin) {
		return customerRepo.save(admin);
	}
	
	public Customer findUserById(Long id) {
		return customerRepo.findById(id).get();
	}

	public List<Customer> getAllUsers() {
		return customerRepo.findAll();
	}

	public void deleteUserById(Long id) {
		customerRepo.deleteById(id);
	}
	
	public List<Customer> findAllNotSyncedData() {
		return customerRepo.findAllNotSyncedData();
	}
	
	public Customer saveRemoteData(Customer customer) {
		customer.setSynced(true);
		
		Long tempCustomerId = customer.getId();
		if(customer.getRemoteId() != null) customer.setId(customer.getRemoteId());
		else customer.setId(0L);
		customer.setRemoteId(tempCustomerId);
		
		System.out.println("Saving Customer with id: " + customer.getId() + " remote id: " + customer.getRemoteId());
		return customerRepo.saveAndFlush(customer);
	}

	public void markAsSynced(Long id, Long serverId) {
		customerRepo.markAsSynced(id, serverId);
	}

}
