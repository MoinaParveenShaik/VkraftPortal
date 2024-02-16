package com.vkraftportal.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.Data;

@Data
@Document(indexName = "timesheet")
public class Timesheet {

	@Id
	private String id;
	private String employeeName;
	private String employeeNumber;
	private String month;
	private String year;
	private String clientName;
	private String supervisorName;
	private String assignmentName;
	private String projectName;
	private String status;
	private String newStatus;
	private String comments;
	private List<DaywiseActivity> timesheetData;
	private String totalWorkingDays;
	private String totalWorkingHours;

	public Timesheet() {
		// TODO Auto-generated constructor stub
	}

	public Timesheet(String id, String employeeName, String employeeNumber, String month, String year,
			String clientName, String supervisorName, String assignmentName, String projectName, String status,
			String newStatus, String comments, List<DaywiseActivity> timesheetData, String totalWorkingDays,
			String totalWorkingHours) {
		super();
		this.id = id;
		this.employeeName = employeeName;
		this.employeeNumber = employeeNumber;
		this.month = month;
		this.year = year;
		this.clientName = clientName;
		this.supervisorName = supervisorName;
		this.assignmentName = assignmentName;
		this.projectName = projectName;
		this.status = status;
		this.newStatus = newStatus;
		this.comments = comments;
		this.timesheetData = timesheetData;
		this.totalWorkingDays = totalWorkingDays;
		this.totalWorkingHours = totalWorkingHours;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getSupervisorName() {
		return supervisorName;
	}

	public void setSupervisorName(String supervisorName) {
		this.supervisorName = supervisorName;
	}

	public String getAssignmentName() {
		return assignmentName;
	}

	public void setAssignmentName(String assignmentName) {
		this.assignmentName = assignmentName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(String newStatus) {
		this.newStatus = newStatus;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<DaywiseActivity> getTimesheetData() {
		return timesheetData;
	}

	public void setTimesheetData(List<DaywiseActivity> timesheetData) {
		this.timesheetData = timesheetData;
	}

	public String getTotalWorkingDays() {
		return totalWorkingDays;
	}

	public void setTotalWorkingDays(String totalWorkingDays) {
		this.totalWorkingDays = totalWorkingDays;
	}

	public String getTotalWorkingHours() {
		return totalWorkingHours;
	}

	public void setTotalWorkingHours(String totalWorkingHours) {
		this.totalWorkingHours = totalWorkingHours;
	}

	@Override
	public String toString() {
		return "Timesheet [id=" + id + ", employeeName=" + employeeName + ", employeeNumber=" + employeeNumber
				+ ", month=" + month + ", year=" + year + ", clientName=" + clientName + ", supervisorName="
				+ supervisorName + ", assignmentName=" + assignmentName + ", projectName=" + projectName + ", status="
				+ status + ", newStatus=" + newStatus + ", comments=" + comments + ", timesheetData=" + timesheetData
				+ ", totalWorkingDays=" + totalWorkingDays + ", totalWorkingHours=" + totalWorkingHours + "]";
	}

}
