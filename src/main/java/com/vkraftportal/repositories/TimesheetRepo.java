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

	Timesheet deleteByEmployeeNumberAndMonthAndYear(String empNumber, String month, String year);

	List<Timesheet> findByStatusAndMonthAndYear(String status, String month, String year);

	List<Timesheet> findByStatus(String string);

	List<Timesheet> findByClientName(String clientName);

	List<Timesheet> findByProjectName(String projectName);
	
	List<Timesheet> findByProjectNameAndMonthAndYear(String projectName, String month, String year);
	
	List<Timesheet> findByNewStatus(String newStatus);

	Timesheet findByEmployeeNumberAndMonthAndYearAndProjectName(String empNumber, String month, String year,
			String projectName);

	List<Timesheet> findByMonthAndYear(String month, String year);

	List<Timesheet> findByMonthAndYearAndNewStatus(String string, String month, String year);

}
