package com.rcn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rcn.dao.LoginRepo;
import com.rcn.model.LogIn;

@Service
public class AppUserDetailsService implements UserDetailsService {
	
	@Autowired
	private LoginRepo logInRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		LogIn logIn = logInRepo.findByUsername(username);
		if(logIn == null) {
			throw new UsernameNotFoundException("User not found");
		}
		return new UserPrincipal(logIn);
	}

}
