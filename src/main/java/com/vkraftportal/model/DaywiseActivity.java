package com.vkraftportal.model;

import lombok.Data;

@Data
public class DaywiseActivity {

	private int date;
	private String leaveOrWorkingDay;
	private String clientName;
	private String assignmentName;
	private String workingHours;
	private String comments;

	public DaywiseActivity() {
		// TODO Auto-generated constructor stub
	}

	public DaywiseActivity(int date, String leaveOrWorkingDay, String clientName, String assignmentName,
			String workingHours, String comments) {
		this.date = date;
		this.leaveOrWorkingDay = leaveOrWorkingDay;
		this.clientName = clientName;
		this.assignmentName = assignmentName;
		this.workingHours = workingHours;
		this.comments = comments;
	}

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public String getLeaveOrWorkingDay() {
		return leaveOrWorkingDay;
	}

	public void setLeaveOrWorkingDay(String leaveOrWorkingDay) {
		this.leaveOrWorkingDay = leaveOrWorkingDay;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getAssignmentName() {
		return assignmentName;
	}

	public void setAssignmentName(String assignmentName) {
		this.assignmentName = assignmentName;
	}

	public String getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(String workingHours) {
		this.workingHours = workingHours;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "DaywiseActivity [date=" + date + ", leaveOrWorkingDay=" + leaveOrWorkingDay + ", clientName="
				+ clientName + ", assignmentName=" + assignmentName + ", workingHours=" + workingHours + ", comments="
				+ comments + "]";
	}

}
