package com.user.secrets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.user.secrets.domain.User;
import com.user.secrets.security.JwtAuthenticationRequest;
import com.user.secrets.service.UserServiceImpl;

public class TestUtil {

	UserServiceImpl userServiceImpl;
	private MockMvc mvc;


	public TestUtil(MockMvc mvc,UserServiceImpl userServiceImpl) {
		this.mvc = mvc;
		this.userServiceImpl =userServiceImpl;
	}

	// create new user object.
	public User createNewUser() {
		String username = RandomStringUtils.randomAlphanumeric(5);
		String password = RandomStringUtils.randomAlphanumeric(5);
		return new User(username, password, RandomStringUtils.randomAlphanumeric(5),
				RandomStringUtils.randomAlphanumeric(5), RandomStringUtils.randomAlphanumeric(5), null, null, null,
				null);
	}

	// perform post operation for the given user and return the result of the
	// opertion.
	public MvcResult persistUser(User user) throws Exception {
		return mvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
			.content(new Gson().toJson(user)))
			.andExpect(status().isCreated())
			.andReturn();
	}

	// extract and return the JsonObject from the MvcResult.
	public JsonObject getJsonFromResult(MvcResult result) throws JsonSyntaxException, UnsupportedEncodingException {
		MockHttpServletResponse response = result.getResponse();
		JsonObject jsonObject = (new JsonParser()).parse(response.getContentAsString())
			.getAsJsonObject();
		return jsonObject;
	}

	// persist user and return the access token generated for that user.
	public String PersistUserAndGetAccessToken(User user) throws Exception {

		// persist user.
		persistUser(user);

		// get access token.
		MvcResult tokenMockResult = mvc.perform(post("/oauth/token").accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(new Gson().toJson(new JwtAuthenticationRequest(user.getUsername(), user.getPassword()))))
			.andReturn();
		JsonObject tokenObject = getJsonFromResult(tokenMockResult);
		// get token string.
		String token = tokenObject.get("token")
			.getAsString();
		return token;
	}

	// get userId from user.
	public Long getUserId(User user) {
		return userServiceImpl.findByUserName(user.getUsername())
			.getId();
	}
}
