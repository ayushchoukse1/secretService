package com.user.secrets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class UserControllerTest {
	@Autowired
	WebApplicationContext context;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	UserServiceImpl userServiceImpl;

	private MockMvc mvc;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.webAppContextSetup(context)
			.addFilter(springSecurityFilterChain)
			.build();
	}

//	@Test
//	public void getPutDeleteWithoutAuthorization() throws Exception {
//		// GET, PUT, DELETE without any authorization.
//		URI uri = new URI("/user");
//		URI uri1 = new URI("/user" + (0 + (int) (Math.random() * 1000)));
//		mvc.perform(get(uri))
//			.andExpect(status().isUnauthorized());
//		mvc.perform(put(uri1))
//			.andExpect(status().isUnauthorized());
//		mvc.perform(delete(uri1))
//			.andExpect(status().isUnauthorized());
//	}
//
//	@Test
//	public void createUser() throws Exception {
//		mvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
//			.content(new Gson().toJson(getUser())))
//			.andExpect(status().isCreated());
//	}
//
//	@Test
//	public void createDuplicateUser() throws Exception {
//		User user = getUser();
//		mvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
//			.content(new Gson().toJson(user)));
//		mvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
//			.content(new Gson().toJson(user)))
//			.andExpect(status().isConflict())
//			.andExpect(content().string("user already exists: " + user.getUsername()));
//	}
//
//	@Test
//	public void updateUserwithoutAuthorization() throws Exception {
//		// update user without access token.
//		User user = getUser();
//		MvcResult mockResult = mvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
//			.content(new Gson().toJson(user)))
//			.andExpect(status().isCreated())
//			.andReturn();
//		MockHttpServletResponse response = mockResult.getResponse();
//		JsonObject jsonObject = (new JsonParser()).parse(response.getContentAsString())
//			.getAsJsonObject();
//		user.setFirstName("ayush");
//		user.setLastName("choukse");
//		user.setEmail("ayush.choukse@gmail.com");
//		mvc.perform(put("/user" + jsonObject.get("id")
//			.getAsString()).contentType(MediaType.APPLICATION_JSON)
//				.content(new Gson().toJson(user)))
//			.andExpect(status().isUnauthorized());
//	}

	@Test
	public void updateUserwithAuthorization() throws Exception {
		// update user with access token.
		User user = getUser();
		System.out.println(user.toString());
		// post user.
		MvcResult postMockResult = mvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
			.content(new Gson().toJson(user)))
			.andExpect(status().isCreated())
			.andReturn();

		// extract id from postMockResult
		MockHttpServletResponse postMockResponse = postMockResult.getResponse();
		JsonObject jsonObject = (new JsonParser()).parse(postMockResponse.getContentAsString())
			.getAsJsonObject();
		String id = jsonObject.get("id")
			.getAsString();
		System.out.println("userId from postMock"+id);
		// get access token.
		MvcResult tokenMockResult = mvc.perform(post("/oauth/token").accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(new Gson().toJson(new JwtAuthenticationRequest(user.getUsername(), user.getPassword()))))
			.andReturn();
		
		//extract token from tokenMockResult.
		MockHttpServletResponse postTokenResponse = tokenMockResult.getResponse();
		JsonObject jsonTokenObject = (new JsonParser()).parse(postTokenResponse.getContentAsString())
			.getAsJsonObject();
		String accessToken = jsonTokenObject.get("token")
			.getAsString();
		System.out.println("token from token mock"+accessToken);
		// update user.
		user.setFirstName("ayush");
		user.setLastName("choukse");
		user.setEmail("ayush.choukse@gmail.com");
		
		// perform put operation.
		mvc.perform(put("/user/" + jsonObject.get("id")
			.getAsString()).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", accessToken)
				.content(new Gson().toJson(user)))
			.andExpect(status().isOk());
	}

	public User getUser() {
		String username = RandomStringUtils.randomAlphanumeric(5);
		String password = RandomStringUtils.randomAlphanumeric(5);
		return new User(username, password, RandomStringUtils.randomAlphanumeric(5),
				RandomStringUtils.randomAlphanumeric(5), RandomStringUtils.randomAlphanumeric(5), null, null, null,
				null);

	}
}
