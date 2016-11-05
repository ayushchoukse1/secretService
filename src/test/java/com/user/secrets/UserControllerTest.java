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
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.user.secrets.domain.Authority;
import com.user.secrets.domain.Secret;
import com.user.secrets.domain.User;
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

	@Test
	public void getPutDeleteWithoutAuthorization() throws Exception {
		// GET, PUT, DELETE without any authorization.
		URI uri = new URI("/user");
		URI uri1 = new URI("/user" + (0 + (int) (Math.random() * 1000)));
		mvc.perform(get(uri))
			.andExpect(status().isUnauthorized());
		mvc.perform(put(uri1))
			.andExpect(status().isUnauthorized());
		mvc.perform(delete(uri1))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void createUser() throws Exception {
		mvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
			.content(new Gson().toJson(getUser())))
			.andExpect(status().isCreated());
	}

	@Test
	public void createDuplicateUser() throws Exception {
		User user = getUser();
		mvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
			.content(new Gson().toJson(user)));
		mvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
			.content(new Gson().toJson(user)))
			.andExpect(status().isConflict())
			.andExpect(content().string("user already exists: " + user.getUsername()));
	}
	
	public void updateUser() throws Exception {
		mvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
			.content(new Gson().toJson(getUser())))
			.andExpect(status().isCreated());
	}
	
	public User getUser() {
		String username = RandomStringUtils.randomAlphanumeric(5);
		String password = RandomStringUtils.randomAlphanumeric(5);
		return new User(username, password, RandomStringUtils.randomAlphanumeric(5),
				RandomStringUtils.randomAlphanumeric(5), RandomStringUtils.randomAlphanumeric(5), null, null, null,
				null);

	}
}
