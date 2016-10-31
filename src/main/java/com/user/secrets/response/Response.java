package com.user.secrets.response;

import org.springframework.http.ResponseEntity;

import com.user.secrets.dao.User;

public interface Response {
	ResponseEntity notFound(Object object);

	ResponseEntity conflict(Object object);

	ResponseEntity created(Object object);

	ResponseEntity ok(Object object);
}
