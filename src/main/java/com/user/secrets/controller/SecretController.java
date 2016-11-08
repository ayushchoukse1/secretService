package com.user.secrets.controller;

import java.util.Date;
import java.util.List;

import org.apache.ws.security.components.crypto.Crypto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.user.secrets.domain.Secret;
import com.user.secrets.domain.User;
import com.user.secrets.response.SecretHTTPResponse;
import com.user.secrets.security.JwtUser;
import com.user.secrets.service.SecretServiceImpl;
import com.user.secrets.service.UserServiceImpl;

@RestController
public class SecretController {

	UserServiceImpl userServiceImpl;
	SecretServiceImpl secretServiceImpl;
	SecretHTTPResponse response;

	@Autowired
	public SecretController(UserServiceImpl userServiceImpl, SecretServiceImpl secretServiceImpl,
			SecretHTTPResponse response) {
		this.userServiceImpl = userServiceImpl;
		this.secretServiceImpl = secretServiceImpl;
		this.response = response;
	}

	@RequestMapping(value = "${home.secrets}", method = RequestMethod.GET)
	public List<Secret> getSecretList() {
		User user = userServiceImpl.findByUserName(getCurrentUser().getUsername());
		return userServiceImpl.findAllSecrets(userServiceImpl.findById(user.getId()));
	}

	@PreAuthorize("hasPermission(authentication, #id)")
	@RequestMapping(value = "${home.secret.operation}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Secret> getSecret(@PathVariable(value = "id") Long id) {
		if (ValidateSecret(id).equals(null)) {
			return response.notFound(getString("secret not found: " + id));
		} else {
			return response.ok(secretServiceImpl.findById(id));
		}
	}

	@RequestMapping(value = "${home.secret}", method = RequestMethod.POST)
	public ResponseEntity<Secret> saveSecret(@RequestBody Secret secret) {
		if (secretServiceImpl.findById(secret.getId()) != null)
			return response.conflict("secret already exist: " + secret.getId());
		JwtUser temp = (JwtUser) SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		secret.setUser(userServiceImpl.findByUserName(temp.getUsername()));
		secret.setCreatedOn(new Date(System.currentTimeMillis()));
		secret.setUpdatedOn(new Date(System.currentTimeMillis()));
		secretServiceImpl.save(secret);
		return response.created(secret);
	}

	@PreAuthorize("hasPermission(authentication, #id)")
	@RequestMapping(value = "${home.secret.operation}", method = RequestMethod.DELETE)
	public ResponseEntity<Secret> deleteSecret(@PathVariable(value = "id") Long id) {
		if (ValidateSecret(id) == null)
			return response.notFound(getString("secret not found: " + id));

		secretServiceImpl.delete(id);
		return response.ok(getString("user deleted: " + id));
	}

	@PreAuthorize("hasPermission(authentication, #id)")
	@RequestMapping(value = "${home.secret.operation}", method = RequestMethod.PUT)
	public ResponseEntity<Secret> updateSecret(@PathVariable(value = "id") Long id, @RequestBody Secret secret) {
		if (ValidateSecret(id) == null)
			return response.notFound(getString("secret not found: " + id));
		JwtUser temp = (JwtUser) SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		Secret secretUpdate = secretServiceImpl.findById(id);
		//secretUpdate.setUser(userServiceImpl.findByUserName(temp.getUsername()));
		if (secret.getBody() != null) {
			secretUpdate.setBody(secret.getBody());
		}
		if (secret.getTitle() != null) {
			secretUpdate.setTitle(secret.getTitle());
		}
		secretUpdate.setUpdatedOn(new Date());
		
		secretServiceImpl.update(secretUpdate);
		return response.ok(secretUpdate);
	}

	public Secret ValidateSecret(Long id) {
		return secretServiceImpl.findById(id);
	}

	private JwtUser getCurrentUser() {
		return (JwtUser) SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
	}
	public String getString(String object) {
		JsonObject object1 = new JsonObject();
		object1.addProperty("message", object);
		return new Gson().toJson(object1);
	}
}
