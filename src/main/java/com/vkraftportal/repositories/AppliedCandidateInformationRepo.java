package com.vkraftportal.repositories;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.vkraftportal.model.AppliedCandidateInformation;


public interface AppliedCandidateInformationRepo extends ElasticsearchRepository<AppliedCandidateInformation, Integer>{
	
	AppliedCandidateInformation findByJobIdAndEmail(String jobId, String email);

	List<AppliedCandidateInformation> deleteByEmail(String email);

	Iterable<AppliedCandidateInformation> findByStatus(String str);

	AppliedCandidateInformation findByEmail(String email);
	
	AppliedCandidateInformation findByJobIdAndRole(String jobId, String role);

	List<AppliedCandidateInformation> countByStatus(String status);

}
