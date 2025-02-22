package rcn.web.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import rcn.web.model.Employee;

public interface EmployeeRepo extends JpaRepository<Employee, Long>{
	
	@Query("FROM Employee emp where emp.name LIKE %:keyword% or emp.designation LIKE %:keyword%")
	Page<Employee> findAll(PageRequest pageRequest, String keyword);
	
}
