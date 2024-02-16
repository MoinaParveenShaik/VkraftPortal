package com.vkraftportal.repositories;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.vkraftportal.model.Timesheet;

@Repository
public interface TimesheetRepo extends ElasticsearchRepository<Timesheet, Integer> {

	Timesheet findByEmployeeName(String name);

	Timesheet findByEmployeeNumber(String empNumber);

	Timesheet findByEmployeeNumberAndMonthAndYear(String employeeNumber, String month, String year);

	void deleteByEmployeeNumberAndMonthAndYear(String empNumber, String month, String year);

	List<Timesheet> findByStatusAndMonthAndYear(String status, String month, String year);

	List<Timesheet> findByStatus(String string);

	List<Timesheet> findByClientName(String clientName);

	List<Timesheet> findByProjectName(String projectName);
	
	Timesheet findByClientNameAndProjectName(String clientName, String projectName);

	List<Timesheet> findByNewStatus(String string);

	List<Timesheet> findByProjectNameAndMonthAndYear(String projectName, String month, String year);

	Timesheet findByEmployeeNumberAndMonthAndYearAndProjectName(String empNumber, String month, String year,
			String projectName);

}
