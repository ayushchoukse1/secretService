package com.user.secrets.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.user.secrets.dao.Secret;
import com.user.secrets.dao.User;
import com.user.secrets.repository.SecretRepository;
import com.user.secrets.repository.UserRepository;

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
	public List<Secret> findAllByUser(User user) {
		return secretRepository.findAllByUser(userRepository.findOne(user.getId()));
	}

}
