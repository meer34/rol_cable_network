package rcn.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired private UserRepo userRepository;

	@Override
	public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
		System.out.println("Phone number is" + phone);
		User user = userRepository.getUserByPhoneNumber(phone);
		
		if(user == null) {
			throw new UsernameNotFoundException("Could not find User with username: " + phone);
		} else {
			System.out.println("Got user with phone: " + user.getPhone());
		}
		
		return new MyUserDetails(user);
	}

}
