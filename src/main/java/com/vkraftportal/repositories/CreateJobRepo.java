package com.vkraftportal.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.vkraftportal.model.CreateJob;

public interface CreateJobRepo extends ElasticsearchRepository<CreateJob, Integer> {
	
	CreateJob findByJobId(String jobId);

	CreateJob findByRole(String role);

}
