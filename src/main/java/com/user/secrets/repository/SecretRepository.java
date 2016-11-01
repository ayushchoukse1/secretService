package com.user.secrets.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.user.secrets.dao.Secret;
import com.user.secrets.dao.User;

public interface SecretRepository extends CrudRepository<Secret, Long> {
	Secret findById(Long id);

	List<Secret> findAll();

	List<Secret> findAllOrderByCreatedOn(Date createdOn);

	List<Secret> findAllOrderByUpdatedOn(Date updatedOn);
	
	//List<Secret> findAllByUser(User user);
	
	Secret findByTitle(String title);
	
	Secret findByTitleLike(String title);
	
	List<Secret> findAllByUser(User user);
}
