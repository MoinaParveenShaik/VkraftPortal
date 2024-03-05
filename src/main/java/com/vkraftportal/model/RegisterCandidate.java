package com.vkraftportal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "registercandidate")
public class RegisterCandidate {

	@Id
	private String id;
	private String fullname;
	private String email;
	private String mobileNumber;
	private String password;

	public RegisterCandidate() {
	}

	public RegisterCandidate(String id, String fullname, String email, String mobileNumber, String password) {
		super();
		this.id = id;
		this.fullname = fullname;
		this.email = email;
		this.mobileNumber = mobileNumber;
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "RegisterCandidate [id=" + id + ", fullname=" + fullname + ", email=" + email + ", mobileNumber="
				+ mobileNumber + ", password=" + password + "]";
	}

}
