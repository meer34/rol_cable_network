package rcn.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import rcn.web.model.IncomeType;

public interface IncomeTypeRepo extends JpaRepository<IncomeType, Long>{
	
}
