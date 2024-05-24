package rcn.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rcn.web.model.AppUser;
import rcn.web.repo.AppUserRepo;

@Service
public class AppUserService {

	@Autowired private AppUserRepo appUserRepo;

	public AppUser saveUserToDB(AppUser appUser) {
		return appUserRepo.save(appUser);
	}
	
	public AppUser findUserById(Long id) {
		return appUserRepo.findById(id).get();
	}

	public List<AppUser> getAllUsers() {
		return appUserRepo.findAll();
	}

	public void deleteUserById(Long id) {
		appUserRepo.deleteById(id);
	}

	public List<AppUser> getAppUsersByPhoneNumberAndIdNotMatching(String phone, Long id) {
		return appUserRepo.findByPhoneAndIdNot(phone, id);
	}
	

}
