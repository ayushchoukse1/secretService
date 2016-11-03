package com.user.secrets.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.user.secrets.domain.Secret;
import com.user.secrets.domain.User;

public interface UserRepository extends CrudRepository<User, Long>{
	

	User findByFirstName(String firstName);

	User findByLastName(String lastName);
	
	User findByFirstNameAndLastName(String firstName, String lastName);

	User findByEmail(String email);
	
	List<User> findAll();

	User findByUsername(String username);
	
}
