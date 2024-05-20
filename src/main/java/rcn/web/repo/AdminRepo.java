package rcn.web.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import rcn.security.User;
import rcn.web.model.Admin;

public interface AdminRepo extends JpaRepository<Admin, Long>{

	List<Admin> findByPhoneAndIdNot(String phone, Long id);
	
	@Query("FROM Admin adm WHERE adm.user = :user")
	Optional<Admin> findByUser(User user);
	
}
