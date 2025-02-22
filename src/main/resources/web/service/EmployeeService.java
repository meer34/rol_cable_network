package rcn.web.service;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import rcn.web.model.Employee;
import rcn.web.repo.EmployeeRepo;

@Service
public class EmployeeService {

	@Autowired private EmployeeRepo employeeRepo;

	public Employee saveEmployeeToDB(Employee employee) {
		return employeeRepo.save(employee);
	}

	public Page<Employee> getAllEmployees(Integer pageNo, Integer pageSize) {
		return employeeRepo.findAll(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
	}
	
	public Employee findEmployeeById(long id) {
		return (Employee) employeeRepo.findById(id).get();
	}

	public Page<Employee> searchEmployeeByNameAndDesignation(String keyword, int pageNo, Integer pageSize) throws ParseException {
		PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
		return employeeRepo.findAll(pageRequest, keyword);

	}

	public void deleteEmployeeById(Long id) {
		employeeRepo.deleteById(id);
	}

}
