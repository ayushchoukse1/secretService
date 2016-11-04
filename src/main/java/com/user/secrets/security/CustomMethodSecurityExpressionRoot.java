package com.user.secrets.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import com.user.secrets.repository.SecretRepository;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot
		implements MethodSecurityExpressionOperations {
	private Object filterObject;
	private Object returnObject;
	private final Log logger = LogFactory.getLog(this.getClass());
	
	public CustomMethodSecurityExpressionRoot(Authentication authentication) {
		super(authentication);
	}

	public boolean hasAccess(Authentication authentication, Long id) {
		if ((((JwtUser) authentication.getPrincipal()).getId()) == id)
			{logger.info((((JwtUser) authentication.getPrincipal()).getId())+" has access to resource "+id);
			return true;
			}
		else {
			logger.error((((JwtUser) authentication.getPrincipal()).getId())+" has no authorization to resource "+id);
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
