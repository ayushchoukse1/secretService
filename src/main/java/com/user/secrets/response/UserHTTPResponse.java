package com.user.secrets.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserHTTPResponse implements Response {

	@Override
	public ResponseEntity notFound(Object object) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(object);
	}

	@Override
	public ResponseEntity conflict(Object object) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(object);
	}

	@Override
	public ResponseEntity ok(Object object) {
		return ResponseEntity.status(HttpStatus.OK).body(object);
	}

	@Override
	public ResponseEntity created(Object object) {
		return ResponseEntity.status(HttpStatus.CREATED).body(object);
	}

}
