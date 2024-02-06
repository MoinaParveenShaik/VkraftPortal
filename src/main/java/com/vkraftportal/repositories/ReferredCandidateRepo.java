package com.vkraftportal.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.vkraftportal.model.ReferredCandidateInformation;

public interface ReferredCandidateRepo extends ElasticsearchRepository<ReferredCandidateInformation, Integer> {
	
	ReferredCandidateInformation findByFullNameAndEmailAndPosition(String fullName, String email, String position);

}
