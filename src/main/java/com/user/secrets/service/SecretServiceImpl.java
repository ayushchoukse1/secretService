package com.user.secrets.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.user.secrets.domain.Secret;
import com.user.secrets.domain.User;
import com.user.secrets.repository.SecretRepository;
import com.user.secrets.repository.UserRepository;

@Service
public class SecretServiceImpl implements SecretService {

	SecretRepository secretRepository;
	UserRepository userRepository;

	@Autowired
	public SecretServiceImpl(SecretRepository secretRepository, UserRepository userRepository) {
		this.secretRepository = secretRepository;
		this.userRepository = userRepository;
	}

	@Override
	public Secret findById(Long id) {
		return secretRepository.findById(id);
	}

	@Override
	public Secret findByTitle(String title) {
		return secretRepository.findByTitleLike(title);
	}

	@Override
	public List<Secret> findByCreateOn(Date createdOn) {
		return secretRepository.findAllOrderByCreatedOn(createdOn);
	}

	@Override
	public List<Secret> findByUpdateOn(Date updatedOn) {
		return secretRepository.findAllOrderByCreatedOn(updatedOn);
	}

	@Override
	public Secret save(Secret secret) {
		secretRepository.save(secret);
		return secret;
	}

	@Override
	public void delete(Long id) {
		secretRepository.delete(id);
	}

	@Override
	public Secret update(Secret secret) {
		secretRepository.save(secret);
		return secret;
	}

	/*
	 * @Override public List<Secret> findAllByUser(User user) { return
	 * secretRepository.findAllByUser(userRepository.findOne(user.getId())); }
	 */
	
	
}
