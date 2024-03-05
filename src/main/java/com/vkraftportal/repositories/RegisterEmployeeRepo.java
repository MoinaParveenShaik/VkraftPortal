package com.vkraftportal.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import com.vkraftportal.model.RegisterEmployee;

@EnableElasticsearchRepositories
public interface RegisterEmployeeRepo extends ElasticsearchRepository<RegisterEmployee, Integer> {

	RegisterEmployee findByEmail(String email);

	RegisterEmployee findByEmailAndPassword(String email, String password);

	RegisterEmployee findByEmployeeNumberOrEmail(String employeeNumber, String email);
	
	RegisterEmployee findByEmployeeNumberAndEmail(String employeeNumber, String email);
	
	RegisterEmployee deleteByEmployeeNumber(String employeeNumber);
	
	RegisterEmployee findByEmployeeNumber(String employeeNumber);

	RegisterEmployee findByRole(String string);
}
