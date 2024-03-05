package com.vkraftportal.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.vkraftportal.model.HumanResource;

//public interface HumanResourceRepo extends ElasticsearchRepository<HumanResource, Integer> {
//
//	HumanResource findByEmployeeNameAndEmployeeNumber(String employeeName, String employeeNumber);
//	
//	HumanResource findByEmail(String email);
//
//	HumanResource findByEmailAndPassword(String email, String password);
//}

public interface HumanResourceRepo extends ElasticsearchRepository<HumanResource, Integer> {
 
	
	HumanResource findByEmail(String email);
 
	HumanResource findByEmailAndPassword(String email, String password);
}
