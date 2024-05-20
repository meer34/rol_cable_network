package rcn.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rcn.web.model.Moderator;
import rcn.web.repo.ModeratorRepo;

@Service
public class ModeratorService {

	@Autowired private ModeratorRepo moderatorRepo;

	public Moderator saveUserToDB(Moderator moderator) {
		return moderatorRepo.save(moderator);
	}
	
	public Moderator findUserById(Long id) {
		return moderatorRepo.findById(id).get();
	}

	public List<Moderator> getAllUsers() {
		return moderatorRepo.findAll();
	}

	public void deleteUserById(Long id) {
		moderatorRepo.deleteById(id);
	}

	public List<Moderator> getModeratorsByPhoneNumberAndIdNotMatching(String phone, Long id) {
		return moderatorRepo.findByPhoneAndIdNot(phone, id);
	}
	

}
