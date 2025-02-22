package rcn.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rcn.web.model.AppUser;
import rcn.web.repo.AppUserRepo;

@Service
public class AppUserService {

	@Autowired private AppUserRepo appUserRepo;

	public AppUser saveAppUserToDB(AppUser appUser) {
		return appUserRepo.save(appUser);
	}
	
	public AppUser findAppUserById(Long id) {
		return appUserRepo.findById(id).get();
	}

	public List<AppUser> getAllAppUsers() {
		return appUserRepo.findAll();
	}

	public void deleteAppUserById(Long id) {
		appUserRepo.deleteById(id);
	}
	
	public List<AppUser> getAppUsersByPhoneNumber(String phone) {
		 return appUserRepo.findByPhone(phone);
	}

	public List<AppUser> getAppUsersByPhoneNumberAndIdNotMatching(String phone, Long id) {
		return appUserRepo.findByPhoneAndIdNot(phone, id);
	}

}
