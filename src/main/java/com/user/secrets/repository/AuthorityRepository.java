package com.user.secrets.repository;

import org.springframework.data.repository.CrudRepository;

import com.user.secrets.domain.Authority;

public interface AuthorityRepository extends CrudRepository<Authority, Long>{
	Authority findById(Long id);
}
