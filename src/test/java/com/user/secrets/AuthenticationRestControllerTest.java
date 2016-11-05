package com.user.secrets;

/*import static org.hamcrest.Matchers.equalTo;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;*/
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.crsh.shell.impl.command.system.help;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.user.secrets.domain.Authority;
import com.user.secrets.domain.Secret;
import com.user.secrets.domain.User;
import com.user.secrets.security.JwtAuthenticationRequest;
import com.user.secrets.service.UserServiceImpl;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthenticationRestControllerTest {

	@Autowired
	WebApplicationContext context;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	UserServiceImpl userServiceImpl;

	@Bean
	MockMvc getMvc() {
		return mvc;
	}

	private MockMvc mvc;
	private TestUtil testUtil;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.webAppContextSetup(context)
			.addFilter(springSecurityFilterChain)
			.build();
		testUtil = new TestUtil(getMvc(), userServiceImpl);
	}

	@Test
	public void getAccessTokenUnauthorizedWithoutBody() throws Exception {
		// Getting access token without providing body.
		mvc.perform(post("/oauth/token").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void getAccessTokenUnauthorizedWithoutUsernamePassword() throws Exception {
		// Getting access token without any username, and password.
		String req = new Gson().toJson(new JwtAuthenticationRequest(null, null));
		mvc.perform(post("/oauth/token").contentType(MediaType.APPLICATION_JSON)
			.content(req))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void getAccessTokenUnauthorizedWithoutRegistering() throws Exception {
		// Getting access token with out registering the user.
		mvc.perform(post("/oauth/token").contentType(MediaType.APPLICATION_JSON)
			.content(new Gson().toJson(new JwtAuthenticationRequest("admin", "admin"))))
			.andExpect(status().isNotFound());
	}

	@Test
	public void getAccessTokenAuthorizedWithUsernamePassword() throws Exception {
		User user = testUtil.createNewUser();
		testUtil.persistUser(user);
		// Performing post operation to get access token for the user.
		mvc.perform(post("/oauth/token").accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(new Gson().toJson(new JwtAuthenticationRequest(user.getUsername(), user.getPassword()))))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("token", is(notNullValue())))
			.andExpect(jsonPath("userName", is(user.getUsername())))
			.andExpect(jsonPath("validity", is(notNullValue())));
	}

	@Test
	public void getAccessTokenAuthorizedWithUserDeletedAfterRegistration() throws Exception {
		// Getting access token after the user is deleted gives not authorized.
		User user = testUtil.createNewUser();
		// save the user.
		testUtil.persistUser(user);
		// delete the user.
		userServiceImpl.delete(userServiceImpl.findByUserName(user.getUsername())
			.getId());
		// get access token.
		mvc.perform(post("/oauth/token").accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(new Gson().toJson(new JwtAuthenticationRequest(user.getUsername(), user.getPassword()))))
			.andExpect(status().isNotFound());
	}

	@Test
	public void getRefreshTokenUnauthorizedWithoutBody() throws Exception {
		// Getting refresh token without providing body.
		mvc.perform(get("/oauth/refresh"))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void getRefreshTokenAuthorizedWithoutToken() throws Exception {
		// Getting refresh token without providing Token.
		mvc.perform(get("/oauth/refresh"))
			.andExpect(status().isBadRequest())
			.andExpect(content().string("no token provided."));
	}

	@Test
	public void getRefreshTokenAuthorizedWithToken() throws Exception {
		// Getting refresh token by providing a valid access token already
		// generated.
		User user = testUtil.createNewUser();
		String token = testUtil.PersistUserAndGetAccessToken(user);
		String username = user.getUsername();
		mvc.perform(get("/oauth/refresh").header("Authorization", token))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("token", is(notNullValue())))
			.andExpect(jsonPath("userName", is(username)))
			.andExpect(jsonPath("validity", is(notNullValue())));
	}

	@Test
	public void getRefreshTokenAuthorizedWithTokenAndUserDeleted() throws Exception {
		// Getting refresh token by providing a valid access token already
		// generated but the user deleted.
		User user = testUtil.createNewUser();
		String token = testUtil.PersistUserAndGetAccessToken(user);
		String username = user.getUsername();
		userServiceImpl.delete(userServiceImpl.findByUserName(username)
			.getId());
		mvc.perform(get("/oauth/refresh").header("Authorization", token))
			.andExpect(status().isNotFound())
			.andExpect(content().string("user with provided username does not exist"));
	}
}
