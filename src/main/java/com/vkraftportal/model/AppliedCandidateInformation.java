package com.vkraftportal.model;



import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "applied_candidate_info")
public class AppliedCandidateInformation {
	
	@Id
	private String id;
 
	private String jobId;
	private String role;
	private String fullName;
	private String education;
	private String resume;
	private String status;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getResume() {
		return resume;
	}
	public void setResume(String resume) {
		this.resume = resume;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "AppliedCandidateInfo [id=" + id + ", jobId=" + jobId + ", role=" + role + ", fullName=" + fullName
				+ ", education=" + education + ", resume=" + resume + ", status=" + status + "]";
	}
	public AppliedCandidateInformation(String id, String jobId, String role, String fullName, String education, String resume,
			String status) {
		this.id = id;
		this.jobId = jobId;
		this.role = role;
		this.fullName = fullName;
		this.education = education;
		this.resume = resume;
		this.status = status;
	}
	public AppliedCandidateInformation() {
	}
 

}
