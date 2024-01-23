package com.vkraftportal.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@Order
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/saveTimesheet", "/registerEmployee", "/updateTimesheet", "/deleteEmployee",
						"/getTimesheet", "/getAllEmployees", "/verifyEmployee", "/getApprovedTimesheeets",
						"/registerHumanResource", "/pendingEmployees", "/verifyHumanResource", "/getEmployeeCount",
						"/registerCandidate", "/verifyCandidate", "/saveAppliedCandidateInformation", "/verifyLogin",
						"/forgetPassword", "/registerJob", "/getAllJobs", "/deleteJobDetails", "/saveRegisterCandidate",
						"/listOfAppliedCandidates", "/selectedForScreening", "/listOfScreeningCandidates",
						"/selectedForTechnicalOne", "/listOfTechnicalRoundOneCandidates", "/selectedForTechnicalTwo",
						"/listOfTechnicalRoundTwoCandidates", "/selectedForHR", "/listOfHRRoundCandidates",
						"/candidatesSelectedInAllRounds", "/listOfSelectedCandidates", "/deleteRecord",
						"/getAppliedCandidatesCount", "/getScreeningCandidatesCount", "/getTechnicalRoundOneCount",
						"/getTechnicalRoundTwoCount", "/getHRRoundCount") // Allow all requests
				.authenticated().and().httpBasic().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf().disable();
	}
	
	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		UserDetails user = User.withDefaultPasswordEncoder().username("Vkraft").password("vmanage").roles("USER")
				.build();
		return new InMemoryUserDetailsManager(user);
	}
}
