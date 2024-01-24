package com.vkraftportal.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import com.vkraftportal.model.RegisterEmployee;

@EnableElasticsearchRepositories
public interface RegisterEmployeeRepo extends ElasticsearchRepository<RegisterEmployee, Integer> {

	RegisterEmployee findByEmployeeNameAndEmployeeNumber(String employeeName, String employeeNumber);

	RegisterEmployee findByEmail(String email);

	RegisterEmployee findByPassword(String password);

	RegisterEmployee findByEmailAndPassword(String email, String password);

	RegisterEmployee findByEmployeeNumberOrEmail(String employeeNumber, String email);
}
