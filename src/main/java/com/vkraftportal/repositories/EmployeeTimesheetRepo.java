package com.vkraftportal.repositories;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.vkraftportal.model.EmployeeTimesheet;

public interface EmployeeTimesheetRepo extends ElasticsearchRepository<EmployeeTimesheet, Integer> {

	EmployeeTimesheet findByEmployeeNumber(String employeeNumber);

	EmployeeTimesheet findByEmployeeNumberAndYear(String employeeNumber, String year);

	EmployeeTimesheet findByEmployeeNumberAndYear(String employeeNumber, int year);

	List<EmployeeTimesheet> findByJanuaryAndYear(String status, int year);

	List<EmployeeTimesheet> findByFebruaryAndYear(String status, int year);

	List<EmployeeTimesheet> findByMarchAndYear(String status, int year);

	List<EmployeeTimesheet> findByAprilAndYear(String status, int year);

	List<EmployeeTimesheet> findByMayAndYear(String status, int year);

	List<EmployeeTimesheet> findByJuneAndYear(String status, int year);

	List<EmployeeTimesheet> findByJulyAndYear(String status, int year);

	List<EmployeeTimesheet> findByAugustAndYear(String status, int year);

	List<EmployeeTimesheet> findBySeptemberAndYear(String status, int year);

	List<EmployeeTimesheet> findByOctoberAndYear(String status, int year);

	List<EmployeeTimesheet> findByNovemberAndYear(String status, int year);

	List<EmployeeTimesheet> findByDecemberAndYear(String status, int year);

}
