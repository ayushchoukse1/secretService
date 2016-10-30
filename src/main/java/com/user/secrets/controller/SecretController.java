package com.user.secrets.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.user.secrets.dao.Secret;
import com.user.secrets.dao.User;
import com.user.secrets.service.SecretServiceImpl;
import com.user.secrets.service.UserServiceImpl;

@RestController
public class SecretController {

	UserServiceImpl userServiceImpl;
	SecretServiceImpl secretServiceImpl;

	@Autowired
	public SecretController(UserServiceImpl userServiceImpl, SecretServiceImpl secretServiceImpl) {
		this.userServiceImpl = userServiceImpl;
		this.secretServiceImpl = secretServiceImpl;
	}

	@RequestMapping(value = "/secrets",method=RequestMethod.GET)
	public List<Secret> getSecretList() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userServiceImpl.findAllSecrets(userServiceImpl.findById(user.getId()));
	}
	
	@RequestMapping(value = "/secret/{id}",method=RequestMethod.GET)
	public @ResponseBody Secret getSecret(@PathVariable(value="id") Long id) {
		//System.out.println(.toString());
		return secretServiceImpl.findById(id);
	}
}
