package com.user.secrets.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.user.secrets.dao.User;

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
}
