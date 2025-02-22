package rcn.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import rcn.web.model.ExpenseType;

public interface ExpenseTypeRepo extends JpaRepository<ExpenseType, Long>{
	
}
