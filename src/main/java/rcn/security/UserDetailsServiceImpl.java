package rcn.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired private UserRepo userRepository;

	@Override
	public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
		log.info("Phone number is: " + phone);
		User user = userRepository.getUserByPhoneNumber(phone);
		
		if(user == null) {
			throw new UsernameNotFoundException("Could not find User with username: " + phone);
		} else {
			log.info("Got user with phone: " + user.getPhone());
		}
		
		return new MyUserDetails(user);
	}

}
