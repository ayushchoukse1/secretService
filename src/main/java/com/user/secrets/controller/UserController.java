package com.user.secrets.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.user.secrets.domain.Authority;
import com.user.secrets.domain.Secret;
import com.user.secrets.domain.User;
import com.user.secrets.repository.AuthorityRepository;
import com.user.secrets.response.UserHTTPResponse;
import com.user.secrets.service.UserServiceImpl;

@Controller

public class UserController {
	UserServiceImpl userServiceImpl;
	UserHTTPResponse response;
	AuthorityRepository authority;

	@Autowired
	public UserController(UserServiceImpl userServiceImpl, UserHTTPResponse response, AuthorityRepository authority) {
		this.userServiceImpl = userServiceImpl;
		this.response = response;
		this.authority = authority;

	}

	@RequestMapping(value = "/")
	public String home() {
		return "auth";
	}
	
	@RequestMapping(value = "/dyno")
	public String dynoFile() {
		return "redirect: wakemydyno.txt";
	}

	@PreAuthorize("hasAccess(authentication, #id)")
	@RequestMapping(value = "${home.user.operation}", method = RequestMethod.GET)
	public ResponseEntity getUser(@PathVariable(value = "id") Long id) {
		if (userServiceImpl.findById(id) == null) {
			return response.notFound(getString("user not found: " + id));
		}
		return response.ok(userServiceImpl.findById(id));
	}

	@RequestMapping(value = "${home.user}", method = RequestMethod.POST)
	public ResponseEntity save(@RequestBody User user) {

		if (userServiceImpl.findByUserName(user.getUsername()) != null)
			return response.conflict("user already exists: " + user.getUsername());

		List<Authority> authList = new ArrayList<Authority>();
		authList.add(authority.findById((long) 2));
		User newUser = new User();
		newUser.setAuthorities(authList);
		newUser.setEnabled(true);
		newUser.setSecrets(new ArrayList<Secret>());
		newUser.setEmail(user.getEmail());
		newUser.setFirstName(user.getFirstName());
		newUser.setLastName(user.getLastName());
		newUser.setPassword(user.getPassword());
		newUser.setLastPasswordResetDate(new Date());
		newUser.setUsername(user.getUsername());
		userServiceImpl.save(newUser);

		return response.created(newUser);
	}

	@PreAuthorize("hasAccess(authentication, #id)")
	@RequestMapping(value = "${home.user.operation}", method = RequestMethod.DELETE)
	public ResponseEntity<String> delete(@PathVariable(value = "id") Long id) {
		if (ValidateUser(id) == null)
			return response.notFound(getString("user not found: " + id));
		userServiceImpl.delete(id);
		return response.ok(getString("user deleted: " + id));
	}

	@PreAuthorize("hasAccess(authentication, #id)")
	@RequestMapping(value = "${home.user.operation}", method = RequestMethod.PUT)
	public ResponseEntity<User> update(@PathVariable(value = "id") Long id, @RequestBody User user) {
		if (ValidateUser(id) == null)
			return response.notFound("user not found: " + id);
		User userUpdate = userServiceImpl.findById(id);
		if (user.getEmail() != null) {
			userUpdate.setEmail(user.getEmail());
		}
		if (user.getFirstName() != null) {
			userUpdate.setFirstName(user.getFirstName());
		}
		if (user.getLastName() != null) {
			userUpdate.setLastName(user.getLastName());
		}
		if (user.getPassword() != null) {
			userUpdate.setPassword(user.getPassword());
			userUpdate.setLastPasswordResetDate(new Date());
		}
		userServiceImpl.save(userUpdate);
		return response.ok(userUpdate);
	}

	public User ValidateUser(Long id) {
		return userServiceImpl.findById(id);
	}

	public String getString(String object) {
		JsonObject object1 = new JsonObject();
		object1.addProperty("message", object);
		return new Gson().toJson(object1);
	}
}