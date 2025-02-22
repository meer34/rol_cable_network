package rcn.web.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import rcn.web.model.Reminder;

public interface ReminderRepo extends JpaRepository<Reminder, Long>, JpaSpecificationExecutor<Reminder>{

	@Query("FROM Reminder reminder WHERE (reminder.status <> 'Sent' OR reminder.status is null) AND reminder.date = CURRENT_DATE")
	List<Reminder> findAllPendingRemindersForToday();

}
