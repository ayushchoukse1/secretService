package com.user.secrets.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.user.secrets.security.JwtAuthenticationRequest;
import com.user.secrets.security.JwtTokenUtil;
import com.user.secrets.security.JwtUser;
import com.user.secrets.service.JwtAuthenticationResponse;
import com.user.secrets.service.JwtUserDetailsServiceImpl;

@RestController
public class AuthenticationRestController {
	private final Log logger = LogFactory.getLog(this.getClass());

	@Value("${oauth.header}") // Authorization
	private String tokenHeader;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsServiceImpl userDetailsService;

	@RequestMapping(value = "${oauth.route.authentication.path}", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest,
			Device device) throws AuthenticationException {
		if (authenticationRequest == null) {
			return new ResponseEntity<String>("No Body in the request.", HttpStatus.BAD_REQUEST);
		}
		String username = authenticationRequest.getUsername();
		String password = authenticationRequest.getPassword();
		if (username == null || password == null) {
			JsonObject object1 = new JsonObject();
			object1.addProperty("message", "username or password empty. ");
			return new ResponseEntity<String>(new Gson().toJson(object1), HttpStatus.BAD_REQUEST);
		}
		final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		JsonObject object1 = new JsonObject();
		object1.addProperty("message", "user does not exists: " + username);
		if (userDetails == null) {
			return new ResponseEntity<String>(new Gson().toJson(object1), HttpStatus.NOT_FOUND);
		}

		logger.info("authenticating user '" + username + "' with the credentials provided.");
		final Authentication authentication = authenticationManager
			.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		SecurityContextHolder.getContext()
			.setAuthentication(authentication);
		final String token = jwtTokenUtil.generateToken(userDetails, device);
		// Return the token
		logger.info("creating access token for user '" + username + "'.");

		return ResponseEntity.ok(new JwtAuthenticationResponse(token, userDetails.getUsername(),
				jwtTokenUtil.getExpirationDateFromToken(token)));
	}

	@RequestMapping(value = "${oauth.route.authentication.refresh}", method = RequestMethod.GET)
	public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {

		String token = request.getHeader(tokenHeader);
		if (request.getHeader(tokenHeader) == null) {
			return new ResponseEntity<String>("no token provided.", HttpStatus.BAD_REQUEST);
		}
		String username = jwtTokenUtil.getUsernameFromToken(token);
		if ( userDetailsService.loadUserByUsername(username) == null) {
			return new ResponseEntity<String>("user with provided username does not exist", HttpStatus.NOT_FOUND);
		}
		JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
		
		if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
			logger.info("creating refresh token for user '" + username + "'.");
			String refreshedToken = jwtTokenUtil.refreshToken(token);
			return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken, username,
					jwtTokenUtil.getExpirationDateFromToken(token)));
		} else {
			logger.error("Bad Request.");
			return ResponseEntity.badRequest()
				.body(null);
		}
	}
}
