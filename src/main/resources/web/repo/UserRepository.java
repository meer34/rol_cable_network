package rcn.web.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import rcn.web.model.User;

public interface UserRepository extends CrudRepository<User, Long>{
	
	@Query("SELECT u from User u where u.phone = :phone")
	public User getUserByPhoneNumber(@Param("phone") String phone);

}
