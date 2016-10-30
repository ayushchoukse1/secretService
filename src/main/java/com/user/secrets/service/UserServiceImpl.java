package com.user.secrets.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.user.secrets.dao.Secret;
import com.user.secrets.dao.User;
import com.user.secrets.repository.CustomUserRepository;
import com.user.secrets.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService,CustomUserRepository {
	UserRepository userRepository;

	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public User findById(Long id) {
		return userRepository.findOne(id);
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public User findByEmailId(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User findByFirstName(String firstName) {
		return userRepository.findByFirstName(firstName);
	}

	@Override
	public User findByLastName(String lastName) {
		return userRepository.findByLastName(lastName);
	}

	@Override
	public User findByFirstNameAndLastName(String firstName, String lastName) {
		return userRepository.findByFirstNameAndLastName(firstName, lastName);
	}

	@Override
	public void save(User user) {
		userRepository.save(user);
	}

	@Override
	public void delete(User user) {
		userRepository.delete(user);
	}

	@Override
	public void delete(Long id) {
		userRepository.delete(id);
	}

	@Override
	public List<Secret> findAllSecrets(User user) {
		return user.getSecret();
	}
	
}
