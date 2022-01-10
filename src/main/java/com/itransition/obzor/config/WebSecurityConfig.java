package com.itransition.obzor.config;

import com.itransition.obzor.repo.OverviewRepository;
import com.itransition.obzor.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomUserDetailsService();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Override
	protected void configure(HttpSecurity https) throws Exception{
		https.authorizeRequests()
				.antMatchers("/**", "/login", "/logout").permitAll()
				.and()
				.formLogin()
				.loginPage("/login")
				.permitAll()
				.defaultSuccessUrl("/")
				.failureUrl("/login?error")
				.and()
				.oauth2Login()
				.loginPage("/login")
				.and()
				.rememberMe()
				.and()
				.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/");
	}

}
