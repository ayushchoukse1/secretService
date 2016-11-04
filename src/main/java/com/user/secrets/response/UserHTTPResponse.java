package com.user.secrets.response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserHTTPResponse implements Response {
	private final Log logger = LogFactory.getLog(this.getClass());

	@Override
	public ResponseEntity notFound(Object object) {
		logger.error(HttpStatus.NOT_FOUND+" :"+object);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(object);
	}

	@Override
	public ResponseEntity conflict(Object object) {
		logger.error(HttpStatus.CONFLICT+" :"+object);
		return ResponseEntity.status(HttpStatus.CONFLICT).body(object);
	}

	@Override
	public ResponseEntity ok(Object object) {
		logger.info(HttpStatus.OK+" :"+object);
		return ResponseEntity.status(HttpStatus.OK).body(object);
	}

	@Override
	public ResponseEntity created(Object object) {
		logger.info(HttpStatus.CREATED+" :"+object);
		return ResponseEntity.status(HttpStatus.CREATED).body(object);
	}

}
