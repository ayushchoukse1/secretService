package com.user.secrets.service;

import java.util.Date;
import java.util.List;

import com.user.secrets.domain.Secret;
import com.user.secrets.domain.User;

public interface SecretService {

	Secret findById(Long id);

	Secret findByTitle(String title);

	List<Secret> findByCreateOn(Date createdOn);

	List<Secret> findByUpdateOn(Date updatedOn);

	// List<Secret> findAllByUser(User user);
	Secret save(Secret secret);
	
	void delete(Long id);
	
	Secret update(Secret secret);
}
