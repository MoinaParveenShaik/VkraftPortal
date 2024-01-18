package com.vkraftportal.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.vkraftportal.model.AppliedCandidateInformation;


public interface AppliedCandidateInformationRepo extends ElasticsearchRepository<AppliedCandidateInformation, Integer>{
	
	AppliedCandidateInformation findByJobIdAndFullName(String jobId, String fullName);

	void deleteByEmail(String email);

	Iterable<AppliedCandidateInformation> findByStatus(String str);
	
	AppliedCandidateInformation findByEmail(String email);

	AppliedCandidateInformation  findByJobIdAndEmail(String jobId, String email);
	


}
