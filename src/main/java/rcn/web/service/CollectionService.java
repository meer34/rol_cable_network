package rcn.web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import rcn.web.model.Bill;
import rcn.web.model.BillPayment;
import rcn.web.model.Collection;
import rcn.web.model.Consumer;
import rcn.web.model.Due;
import rcn.web.model.DuePayment;
import rcn.web.repo.BillRepo;
import rcn.web.repo.CollectionRepo;
import rcn.web.repo.ConsumerRepo;
import rcn.web.repo.DueRepo;
import rcn.web.specification.CollectionSearchSpecification;
import rcn.web.util.SearchSpecificationBuilder;

@Service
public class CollectionService {

	@Autowired private CollectionRepo collectionRepo;
	@Autowired private BillRepo billRepo;
	@Autowired private DueRepo dueRepo;
	@Autowired private ConsumerRepo consumerRepo;

	public Collection save(Collection collection) {
		Collection existingCollection = null;
		List<Bill> bills = collection.getBills();
		List<Due> dues = collection.getDues();

		if (collection.getId() != null) {
			existingCollection = collectionRepo.findById(collection.getId()).orElse(collection);
			
			if (collection.getBillPayments() == null) {
				collection.setBillPayments(new ArrayList<>());
			}
			if (collection.getDuePayments() == null) {
				collection.setDuePayments(new ArrayList<>());
			}

			collection.getBillPayments().clear(); // Clear existing references
			collection.getDuePayments().clear(); // Clear existing references
		}

		if (bills != null) {
			for (Bill bill : bills) {
				if (bill.getCollectedAmount() != 0) {
					BillPayment billPayment = new BillPayment();
					billPayment.setAmount(bill.getCollectedAmount());
					billPayment.setBill(bill);
					billPayment.setCollection(collection);
					collection.addBillPayment(billPayment);
				}
			}
		}

		if (dues != null) {
			for (Due due : dues) {
				if (due.getCollectedAmount() != 0) {
					DuePayment duePayment = new DuePayment();
					duePayment.setAmount(due.getCollectedAmount());
					duePayment.setDue(due);
					duePayment.setCollection(collection);
					collection.addDuePayment(duePayment);
				}
			}
		}

		// Handle advance amount logic
		Double advanceAmount = collection.getAdvanceAmount();
		if(existingCollection != null) advanceAmount -= existingCollection.getAdvanceAmount();
		
		if (advanceAmount != null) {
			Consumer consumer = collection.getConsumer();
			consumerRepo.deductFromAdvanceAmountForId(consumer.getId(), advanceAmount);
		}

		return collectionRepo.save(collection);
	}


	public Collection getById(Long id) {
		return collectionRepo.findById(id).orElse(null);
	}

	public List<Collection> getAll() {
		return collectionRepo.findAll();
	}

	public Page<Collection> getAll(Integer pageNo, Integer pageSize) {
		return collectionRepo.findAll(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}

	public void deleteById(Long id) {
		Collection collection = collectionRepo.findById(id).orElse(null);

		Double advanceAmount = collection.getAdvanceAmount();
		if(advanceAmount != null && advanceAmount > 0) {
			Consumer consumer = collection.getConsumer();
			consumerRepo.deductFromAdvanceAmountForId(consumer.getId(), advanceAmount);
		}
		collectionRepo.deleteById(id);
	}

	public List<Collection> getAllByConsumer(Long consumerId, Integer pageNo, Integer pageSize) {
		return collectionRepo.findAllForConsumer(consumerId);
	}

	public Page<Collection> getPageByConsumer(Long consumerId, Integer pageNo, Integer pageSize) {
		return collectionRepo.findAllForConsumer(consumerId, PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}

	public Page<Collection> getPageByAppUser(Long appUserId, Integer pageNo, Integer pageSize) {
		return collectionRepo.findAllForAppUser(appUserId, PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}

	public Page<Collection> getPageByAppUserName(String appUserName, Integer pageNo, Integer pageSize) {
		return collectionRepo.findAllForAppUserName(appUserName, PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}

	public Page<Collection> searchCollectionByDateAndKeyword(String keyword, 
			String fromDate, String toDate, int pageNo, Integer pageSize) {
		PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
		CollectionSearchSpecification spec = (CollectionSearchSpecification) SearchSpecificationBuilder.build(fromDate, toDate, keyword, Collection.class);
		return collectionRepo.findAll(spec, pageRequest);
	}

}
