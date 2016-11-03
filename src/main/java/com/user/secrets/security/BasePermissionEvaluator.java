package com.user.secrets.security;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import com.user.secrets.domain.User;
import com.user.secrets.repository.SecretRepository;
import com.user.secrets.repository.UserRepository;

@Configuration
public class BasePermissionEvaluator implements PermissionEvaluator {

	SecretRepository secretRepository;

	public BasePermissionEvaluator() {
		super();
	}

	@Autowired
	public BasePermissionEvaluator(SecretRepository secretRepository) {
		this.secretRepository = secretRepository;
	
	}

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

		JwtUser user = (JwtUser) authentication.getPrincipal();
		String username = secretRepository.findOne((Long) permission).getUser().getUsername();
		if (user.getUsername().equals(username)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
			Object permission) {
		throw new RuntimeException("Id and Class permissions are not supperted by this application");
	}

	public boolean hasAccess(Authentication authentication, Long id) {
		System.out.println("has access: " + authentication.getName());
		JwtUser user = (JwtUser) authentication.getPrincipal();
		System.out.println(" id =  " + id);
		String username = secretRepository.findById(id).getUser().getPassword();
		if (user.getUsername() == username) {
			return true;
		} else {
			return false;
		}
	}

}
