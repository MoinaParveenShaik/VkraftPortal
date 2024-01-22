package com.vkraftportal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "appliedcandidateinformation")
public class AppliedCandidateInformation {

	@Id
	private String id;

	private String fullName;
	private String email;
	private String mobileNumber;
	private String education;
	private String noticePeriod;
	private String relevantExperience;
	private String preferredLocation;
	private String resume;
	private String jobId;
	private String role;
	private String status;

	public AppliedCandidateInformation() {
	}

	public AppliedCandidateInformation(String id, String fullName, String email, String mobileNumber, String education,
			String noticePeriod, String relevantExperience, String preferredLocation, String resume, String jobId,
			String role, String status) {
		super();
		this.id = id;
		this.fullName = fullName;
		this.email = email;
		this.mobileNumber = mobileNumber;
		this.education = education;
		this.noticePeriod = noticePeriod;
		this.relevantExperience = relevantExperience;
		this.preferredLocation = preferredLocation;
		this.resume = resume;
		this.jobId = jobId;
		this.role = role;
		this.status = status;
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

	public String getNoticePeriod() {
		return noticePeriod;
	}

	public void setNoticePeriod(String noticePeriod) {
		this.noticePeriod = noticePeriod;
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

	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "AppliedCandidateInformation [id=" + id + ", fullName=" + fullName + ", email=" + email
				+ ", mobileNumber=" + mobileNumber + ", education=" + education + ", noticePeriod=" + noticePeriod
				+ ", relevantExperience=" + relevantExperience + ", preferredLocation=" + preferredLocation
				+ ", resume=" + resume + ", jobId=" + jobId + ", role=" + role + ", status=" + status + "]";
	}

}
