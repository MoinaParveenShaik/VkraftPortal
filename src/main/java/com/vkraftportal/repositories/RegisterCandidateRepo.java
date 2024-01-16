package com.vkraftportal.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import com.vkraftportal.model.RegisterCandidate;

@EnableElasticsearchRepositories
public interface RegisterCandidateRepo extends ElasticsearchRepository<RegisterCandidate, Integer> {

	RegisterCandidate findByEmail(String email);

	RegisterCandidate findByEmailAndPassword(String email, String password);
}
