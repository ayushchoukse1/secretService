package com.user.secrets.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.user.secrets.domain.Secret;
import com.user.secrets.domain.User;

@Service
public interface UserService {
	
	User findById(Long id);

	List<User> findAll();

	User findByFirstName(String firstName);

	User findByLastName(String lastName);

	User findByFirstNameAndLastName(String firstName, String lastName);

	User findByEmailId(String email);

	void save(User user);

	void delete(User user);

	void delete(Long id);

	User update(User user);
	
	List<Secret> findAllSecrets(User user);
	User findByUserName(String username);
}
