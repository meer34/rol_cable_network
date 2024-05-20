package rcn.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import rcn.web.model.Reminder;
import rcn.web.repo.ReminderRepo;
import rcn.web.specification.ReminderSearchSpecification;
import rcn.web.util.SearchSpecificationBuilder;

@Service
public class ReminderService {

	@Autowired
	private ReminderRepo reminderRepo;

	public Reminder saveReminderToDB(Reminder reminder) {
		return reminderRepo.save(reminder);
	}

	public Reminder findReminderById(Long id) {
		return reminderRepo.findById(id).get();
	}

	public Page<Reminder> getAllReminders(Integer pageNo, Integer pageSize) {
		return reminderRepo.findAll(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}
	
	public List<Reminder> getAllPendingRemindersForToday() {
		return reminderRepo.findAllPendingRemindersForToday();
	}

	public Page<Reminder> searchReminderByDateAndKeyword(String keyword, 
			String fromDate, String toDate, int pageNo, Integer pageSize) {

		PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
		ReminderSearchSpecification spec = (ReminderSearchSpecification) SearchSpecificationBuilder.build(fromDate, toDate, keyword, Reminder.class);
		return reminderRepo.findAll(spec, pageRequest);

	}

	public void deleteReminderById(Long id) {
		reminderRepo.deleteById(id);
	}

}
