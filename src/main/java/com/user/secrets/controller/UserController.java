package com.user.secrets.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.user.secrets.dao.User;
import com.user.secrets.response.UserHTTPResponse;
import com.user.secrets.service.UserServiceImpl;

@Controller
public class UserController {

	UserServiceImpl userServiceImpl;
	UserHTTPResponse response;

	@Autowired
	public UserController(UserServiceImpl userServiceImpl, UserHTTPResponse response) {
		this.userServiceImpl = userServiceImpl;
		this.response = response;
	}

	@RequestMapping("/")
	public String home() {
		return "index";
	}

	@RequestMapping("/login")
	public String login() {
		return "login";
	}

	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity getUser(@PathVariable(value = "id") Long id) {
		if (userServiceImpl.findById(id) == null) {
			return response.notFound("user not found: " + id);
		}
		return response.ok(userServiceImpl.findById(id));
	}

	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity save(@RequestBody User user) {
		if (userServiceImpl.findById(user.getId()) != null)
			return response.conflict("user already exists: " + user.getId());
		userServiceImpl.save(user);
		return response.created(user);
	}

	@RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<String> delete(@PathVariable(value = "id") Long id) {
		if (ValidateUser(id) == null)
			return response.notFound("user not found: " + id);
		userServiceImpl.delete(id);
		return response.ok("user deleted: " + id);
	}

	@RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
	public ResponseEntity<User> update(@PathVariable(value = "id") Long id, @RequestBody User user) {
		if (ValidateUser(id) == null)
			return response.notFound("user not found: " + id);
		userServiceImpl.save(user);
		return response.ok(user);
	}

	public User ValidateUser(Long id) {
		return userServiceImpl.findById(id);
	}
}