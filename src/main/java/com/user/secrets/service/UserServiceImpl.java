package com.user.secrets.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.secrets.domain.Secret;
import com.user.secrets.domain.User;
import com.user.secrets.repository.SecretRepository;
import com.user.secrets.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	UserRepository userRepository;
	SecretRepository secretRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, SecretRepository secretRepository) {
		this.userRepository = userRepository;
		this.secretRepository = secretRepository;
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
		user.setPassword(passwordEncoder.encode(user.getPassword()));
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
		return secretRepository.findAllByUser(user);
	}

	@Override
	public User update(User user) {
		User userUpdate = userRepository.findOne(user.getId());
		userUpdate.setFirstName(user.getFirstName());
		userUpdate.setEmail(user.getEmail());
		userUpdate.setEnabled(user.getEnabled());
		// userUpdate.setAuthorities(user.getAuthorities());
		userUpdate.setLastName(user.getLastName());
		userUpdate.setLastPasswordResetDate(user.getLastPasswordResetDate());
		userUpdate.setUsername(user.getUsername());
		return userRepository.save(userUpdate);
	}

	@Override
	public User findByUserName(String username) {

		return userRepository.findByUsername(username);
	}
}
