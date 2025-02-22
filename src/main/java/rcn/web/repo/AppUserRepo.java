package rcn.web.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import rcn.security.User;
import rcn.web.model.AppUser;

public interface AppUserRepo extends JpaRepository<AppUser, Long>{

	List<AppUser> findByPhoneAndIdNot(String phone, Long id);
	
	@Query("FROM AppUser a WHERE a.user = :user")
	Optional<AppUser> findByUser(User user);

	List<AppUser> findByPhone(String phone);
	
}
