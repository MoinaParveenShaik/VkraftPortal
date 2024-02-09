package com.vkraftportal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "registeremployee")
public class RegisterEmployee {

	@Id
	private String id;

	private String employeeName;
	private String employeeNumber;
	private String email;
	private String password;
	private String role;
	private String designation;
	private String gender;
	private long mobileNumber;

	public RegisterEmployee() {
		// TODO Auto-generated constructor stub
	}

	public RegisterEmployee(String id, String employeeName, String employeeNumber, String email, String password,
			String role, String designation, String gender, long mobileNumber) {
		super();
		this.id = id;
		this.employeeName = employeeName;
		this.employeeNumber = employeeNumber;
		this.email = email;
		this.password = password;
		this.role = role;
		this.designation = designation;
		this.gender = gender;
		this.mobileNumber = mobileNumber;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public long getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(long mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Override
	public String toString() {
		return "RegisterEmployee [id=" + id + ", employeeName=" + employeeName + ", employeeNumber=" + employeeNumber
				+ ", email=" + email + ", password=" + password + ", role=" + role + ", designation=" + designation
				+ ", gender=" + gender + ", mobileNumber=" + mobileNumber + "]";
	}

}