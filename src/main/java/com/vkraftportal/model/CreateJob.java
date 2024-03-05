package com.vkraftportal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "jobs")
public class CreateJob {
	
	@Id
	private String id;
	private String jobId;
	private String role;
	private double experience;
	private String description;
	private int numberOfVacancies;
	private String criteria;
	private String lastDateToApply;
 
	public CreateJob() {
 
	}
 
	public CreateJob(String id, String jobId, String role, double experience, String description, int numberOfVacancies,
			String criteria, String lastDateToApply) {
		super();
		this.id = id;
		this.jobId = jobId;
		this.role = role;
		this.experience = experience;
		this.description = description;
		this.numberOfVacancies = numberOfVacancies;
		this.criteria = criteria;
		this.lastDateToApply = lastDateToApply;
	}
 
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
 
	public double getExperience() {
		return experience;
	}
 
	public void setExperience(double experience) {
		this.experience = experience;
	}
 
	public String getDescription() {
		return description;
	}
 
	public void setDescription(String description) {
		this.description = description;
	}
 
	public int getNumberOfVacancies() {
		return numberOfVacancies;
	}
 
	public void setNumberOfVacancies(int numberOFVacancies) {
		this.numberOfVacancies = numberOFVacancies;
	}
 
	public String getCriteria() {
		return criteria;
	}
 
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
 
	public String getLastDateToApply() {
		return lastDateToApply;
	}
 
	public void setLastDateToApply(String lastDateToApply) {
		this.lastDateToApply = lastDateToApply;
	}
 
	@Override
	public String toString() {
		return "CreateJob [id=" + id + ", jobId=" + jobId + ", role=" + role + ", experience=" + experience
				+ ", description=" + description + ", numberOfVacancies=" + numberOfVacancies + ", criteria=" + criteria
				+ ", lastDateToApply=" + lastDateToApply + "]";
	}

}
