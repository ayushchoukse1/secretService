package com.user.secrets.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.user.secrets.dao.User;
import com.user.secrets.service.UserServiceImpl;

@Controller
public class UserController {

	UserServiceImpl userServiceImpl;

	@Autowired
	public UserController(UserServiceImpl userServiceImpl) {
		this.userServiceImpl = userServiceImpl;
	}

	@RequestMapping("/")
	public String home() {
		return "home";
	}

	@RequestMapping("/login")
	public String login() {
		return "login";
	}

	@RequestMapping(value ="/user/{id}",method =RequestMethod.GET)
	public @ResponseBody User getUser(@PathVariable(value="id") Long id) {
		return userServiceImpl.findById(id);
	}
	
}
