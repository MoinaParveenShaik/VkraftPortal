package com.vkraftportal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "referredcandidateinformation")
public class ReferredCandidateInformation {

	@Id
	private String id;

	private String fullName;
	private String email;
	private String mobileNumber;
	private String education;
	private String address;
	private String position;
	private String relevantExperience;
	private String preferredLocation;

	public ReferredCandidateInformation() {
	}

	public ReferredCandidateInformation(String id, String fullName, String email, String mobileNumber, String education,
			String address, String position, String relevantExperience, String preferredLocation) {
		super();
		this.id = id;
		this.fullName = fullName;
		this.email = email;
		this.mobileNumber = mobileNumber;
		this.education = education;
		this.address = address;
		this.position = position;
		this.relevantExperience = relevantExperience;
		this.preferredLocation = preferredLocation;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
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

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getRelevantExperience() {
		return relevantExperience;
	}

	public void setRelevantExperience(String relevantExperience) {
		this.relevantExperience = relevantExperience;
	}

	public String getPreferredLocation() {
		return preferredLocation;
	}

	public void setPreferredLocation(String preferredLocation) {
		this.preferredLocation = preferredLocation;
	}

	@Override
	public String toString() {
		return "ReferredCandidateInformation [id=" + id + ", fullName=" + fullName + ", email=" + email + ", mobileNumber="
				+ mobileNumber + ", education=" + education + ", address=" + address + ", position=" + position
				+ ", relevantExperience=" + relevantExperience + ", preferredLocation=" + preferredLocation + "]";
	}
}
