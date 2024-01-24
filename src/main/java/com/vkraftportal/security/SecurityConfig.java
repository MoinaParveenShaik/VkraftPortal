package com.vkraftportal.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Order
public class SecurityConfig extends WebSecurityConfigurerAdapter {
 
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
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
                        "/getTechnicalRoundTwoCount", "/getHRRoundCount") 
                    .authenticated()
                    .and()
            .httpBasic()
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) 
                    .and()
            .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
            .csrf().disable()
            .exceptionHandling()
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) 
                    .and()
            .addFilterBefore(new CustomFilter(), UsernamePasswordAuthenticationFilter.class); 
    }

 
	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		UserDetails user = User.withDefaultPasswordEncoder().username("Vkraft").password("vmanage").roles("USER")
			.build();
		return new InMemoryUserDetailsManager(user);
	}
}
