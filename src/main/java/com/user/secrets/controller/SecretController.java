package com.user.secrets.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.user.secrets.dao.Secret;
import com.user.secrets.dao.User;
import com.user.secrets.response.SecretHTTPResponse;
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

	@RequestMapping(value = "/secrets", method = RequestMethod.GET)
	public List<Secret> getSecretList() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userServiceImpl.findAllSecrets(userServiceImpl.findById(user.getId()));
	}

	@RequestMapping(value = "/secret/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Secret> getSecret(@PathVariable(value = "id") Long id) {
		if (ValidateSecret(id) == null) {
			return response.notFound("secret not found: " + id);
		} else {
			return response.ok(secretServiceImpl.findById(id));
		}
	}

	@RequestMapping(value = "/secret", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Secret> saveSecret(@RequestBody Secret secret) {
		if (secretServiceImpl.findById(secret.getId()) != null)
			return response.conflict("secret already exist: " + secret.getId());

		secretServiceImpl.save(secret);
		return response.created(secret);
	}

	@RequestMapping(value = "/secret/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Secret> deleteSecret(@PathVariable(value = "id") Long id) {
		if (ValidateSecret(id) == null)
			return response.notFound("secret not found: " + id);

		secretServiceImpl.delete(id);
		return response.ok("user deleted: " + id);
	}

	@RequestMapping(value = "/secret/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Secret> updateSecret(@PathVariable(value = "id") Long id, @RequestBody Secret secret) {
		if (ValidateSecret(id) == null)
			return response.notFound("secret not found: " + id);
		secretServiceImpl.update(secret);
		return response.ok(secret);

	}

	public Secret ValidateSecret(Long id) {
		return secretServiceImpl.findById(id);
	}

}
