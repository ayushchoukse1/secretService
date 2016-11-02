package com.user.secrets.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import com.user.secrets.dao.User;
import com.user.secrets.repository.SecretRepository;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot
		implements MethodSecurityExpressionOperations {
	private Object filterObject;
	private Object returnObject;

	@Autowired
	SecretRepository secretRepository;

	public CustomMethodSecurityExpressionRoot(Authentication authentication) {
		super(authentication);
	}

	public boolean isAllowed(Long id) {
		System.out.println("isAllowed: " + authentication.getName());
		JwtUser user = (JwtUser) authentication.getPrincipal();
		System.out.println(" id =  " + id);
		String username = secretRepository.findById(id).getUser().getPassword();
		if (user.getUsername() == username) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setFilterObject(Object filterObject) {
		this.filterObject = filterObject;
	}

	@Override
	public Object getFilterObject() {

		return filterObject;
	}

	@Override
	public void setReturnObject(Object returnObject) {
		this.returnObject = returnObject;
	}

	@Override
	public Object getReturnObject() {

		return returnObject;
	}

	@Override
	public Object getThis() {

		return this;
	}

}
