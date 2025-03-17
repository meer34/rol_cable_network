package rcn.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = "rcn")
@PropertySource("classpath:auth_mapper.properties")
@EnableMethodSecurity(prePostEnabled = true)
@Slf4j
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired private Environment environment;
	@Autowired private UserDetailsServiceImpl userDetailsService;


	@Bean
	protected AuthenticationProvider authProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(new BCryptPasswordEncoder());

		return authProvider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		.headers().frameOptions().disable();

		processAuthorization(http, true)
		.and()
		.formLogin()
		.loginPage("/login")
		.usernameParameter("phone")
		.permitAll()
		.and()
		.logout()
		.logoutUrl("/logout")
		.permitAll();

	}

	private ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry processAuthorization(HttpSecurity http, boolean enableSecurity) throws Exception {

		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlRegistry = http.authorizeRequests();
		
		if(!enableSecurity) return urlRegistry.anyRequest().permitAll();
		
		String[] allPermitUrls = environment.getProperty("all-permit-urls").split("~");
//		String[] authPermitUrls = environment.getProperty("authentication-based-urls").split("~");
//		String[] restrictedUrls = environment.getProperty("authority-based-urls").split("~");

		urlRegistry.antMatchers("/rest-login").permitAll();
		urlRegistry.antMatchers("/get_*").authenticated();

		/*
		for (String restrictedUrl : restrictedUrls) {
			log.info("###############" + restrictedUrl);

			String[] authorities = environment.getProperty(restrictedUrl).split(",");
			if(authorities == null) continue;

			urlRegistry.antMatchers("/" + restrictedUrl + "/**").hasAnyAuthority(authorities);

		}*/

		/*
		for (String auth : authPermitUrls) {
			urlRegistry.antMatchers("/" + auth).authenticated();
		}
		*/

		for (String all : allPermitUrls) {
			urlRegistry.antMatchers("/" + all).permitAll();
		}

		return urlRegistry.anyRequest().authenticated();
	}

}