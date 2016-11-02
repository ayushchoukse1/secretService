package com.user.secrets.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import com.user.secrets.repository.SecretRepository;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

	SecretRepository secretRepository;
	
	public MethodSecurityConfig() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public MethodSecurityConfig(SecretRepository secretRepository) {
		super();
		this.secretRepository = secretRepository;
	}

	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		final CustomMethodSecurityExpressionHandler expressionHandler = new CustomMethodSecurityExpressionHandler();
		expressionHandler.setPermissionEvaluator(new BasePermissionEvaluator(secretRepository));
		return expressionHandler;
	}
}
