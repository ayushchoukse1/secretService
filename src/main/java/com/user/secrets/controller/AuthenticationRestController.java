package com.user.secrets.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.user.secrets.security.JwtAuthenticationRequest;
import com.user.secrets.security.JwtTokenUtil;
import com.user.secrets.security.JwtUser;
import com.user.secrets.service.JwtAuthenticationResponse;
import com.user.secrets.service.JwtUserDetailsServiceImpl;

@RestController
public class AuthenticationRestController {
	private final Log logger = LogFactory.getLog(this.getClass());

	@Value("${jwt.header}") // Authorization
	private String tokenHeader;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsServiceImpl userDetailsService;

	@RequestMapping(value = "${jwt.route.authentication.path}", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest,
			Device device) throws AuthenticationException {

		String username = authenticationRequest.getUsername();
		String password = authenticationRequest.getPassword();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		if (userDetails == null) {
			ResponseEntity.ok("user does not exist: " + username);
		}
		logger.info("authenticating user '" + username + "' with the credentials provided.");

		final Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		final String token = jwtTokenUtil.generateToken(userDetails, device);

		// Return the token
		logger.info("creating access token for user '" + username + "'.");

		return ResponseEntity.ok(new JwtAuthenticationResponse(token, userDetails.getUsername(),
				jwtTokenUtil.getExpirationDateFromToken(token)));
	}

	@RequestMapping(value = "${jwt.route.authentication.refresh}", method = RequestMethod.GET)
	public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {

		String token = request.getHeader(tokenHeader);
		String username = jwtTokenUtil.getUsernameFromToken(token);

		JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
		if (user == null) {
			ResponseEntity.ok("user does not exist: " + username);
		}
		
		if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {

			logger.info("creating refresh token for user '" + username + "'.");

			String refreshedToken = jwtTokenUtil.refreshToken(token);
			return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken, username,
					jwtTokenUtil.getExpirationDateFromToken(token)));
		} else {
			logger.error("Bad Request.");
			return ResponseEntity.badRequest().body(null);
		}
	}
}
