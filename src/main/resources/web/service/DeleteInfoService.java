package com.hunter.web.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;

import com.hunter.data.controller.RepositoryConfigurer;
import com.hunter.web.model.Admin;
import com.hunter.web.model.Customer;
import com.hunter.web.model.DeleteInfo;
import com.hunter.web.model.Expense;
import com.hunter.web.model.ExpenseType;
import com.hunter.web.model.Income;
import com.hunter.web.model.IncomeType;
import com.hunter.web.model.Moderator;
import com.hunter.web.model.Party;
import com.hunter.web.model.Product;
import com.hunter.web.model.Reminder;
import com.hunter.web.model.StockIn;
import com.hunter.web.model.StockOut;
import com.hunter.web.model.StockOutProduct;
import com.hunter.web.model.User;
import com.hunter.web.repo.DeleteInfoRepo;

@Service
public class DeleteInfoService {

	@Autowired ApplicationContext appContext;
	@Autowired RepositoryConfigurer repositoryConfigurer;

	@Autowired private DeleteInfoRepo deleteInfoRepo;

	public void storeDeleteInfoInDB(String className, Long deleteId) {
		deleteInfoRepo.save(new DeleteInfo(className, deleteId));
	}

	public Map<String, Set<Long>> getAllDeleteInfo() {
		List<DeleteInfo> listOfDeleteInfo = deleteInfoRepo.findAll();
		Map<String, Set<Long>> mapOfAllDeletedRecords = new HashMap<>();

		for (DeleteInfo deleteInfo : listOfDeleteInfo) {
			if(deleteInfo.getDeletedId() == null) continue;
			if(!mapOfAllDeletedRecords.containsKey(deleteInfo.getClassName())) {
				mapOfAllDeletedRecords.put(deleteInfo.getClassName(), new HashSet<>());
			}
			mapOfAllDeletedRecords.get(deleteInfo.getClassName()).add(deleteInfo.getDeletedId());
		}
		return mapOfAllDeletedRecords;
	}

	public List<String> deleteRemoteData(Map<String, Set<Long>> mapOfDeleteRecords) {
		List<String> ackList = new ArrayList<>();
		try {
			Class<?>[] classArr = {
					Reminder.class,
					StockOutProduct.class,
					Product.class,
					StockOut.class,
					StockIn.class,
					Customer.class,
					Party.class,
					Income.class,
					Expense.class,
					IncomeType.class,
					ExpenseType.class,
					Moderator.class,
					Admin.class,
					User.class
			};

			String className = null;
			for (Class<?> cls : classArr) {
				className = cls.getSimpleName();
				if(mapOfDeleteRecords.get(className) != null) {
					for (Long id : mapOfDeleteRecords.get(className)) {
						deleteData(cls, id);
					}
					ackList.add(className);
				}
			}

		} catch(Exception e) {
			e.printStackTrace();
		}

		return ackList;
	}

	@Transactional
	public Long processAcknowledgement(String className) {
		return deleteInfoRepo.deleteByClassName(className);
	}

	@SuppressWarnings("unchecked")
	public JpaRepository<Entity, Serializable> getRepository(Class<?> javaClass) throws Exception {
		Repositories repositories = repositoryConfigurer.getRepositories();
		return (JpaRepository<Entity, Serializable>) repositories.getRepositoryFor(javaClass)
				.orElseThrow(() -> new Exception("Unable to get repo for class - " + javaClass.getSimpleName()));
	}

	public void deleteData(Class<?> cls, Long id) {
		try {
			getRepository(cls).deleteById(id);
		} catch(Exception e) {
			System.out.println("No data to delete with id - " + id);
		}
	}

}
