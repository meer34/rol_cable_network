package com.rcn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AppUserDetailsService userDetailsService;
	
	@Bean
	public AuthenticationProvider authProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(new BCryptPasswordEncoder());
		
		return authProvider;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.authorizeRequests()
			.antMatchers("/login", "/css/**", "/images/**", "/js/**").permitAll()
			.anyRequest().authenticated()
		.and()
			.formLogin()
			.loginPage("/login")
		.and()
			.logout()
			.logoutUrl("/logout");
	}
	
//	@Bean
//	@Override
//	protected UserDetailsService userDetailsService() {
//		List<UserDetails> users = new ArrayList<>();
//		users.add(User.withDefaultPasswordEncoder().username("Meer").password("Meer@5349").roles("USER").build());
//		return new InMemoryUserDetailsManager(users);
//	}
	
}
