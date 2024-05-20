package rcn.web.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import rcn.security.User;
import rcn.web.model.Moderator;

public interface ModeratorRepo extends JpaRepository<Moderator, Long>{

	List<Moderator> findByPhoneAndIdNot(String phone, Long id);
	
	@Query("FROM Moderator m WHERE m.user = :user")
	Optional<Moderator> findByUser(User user);
	
}
