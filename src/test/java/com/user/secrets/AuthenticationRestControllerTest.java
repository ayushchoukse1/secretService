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
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.user.secrets.domain.User;
import com.user.secrets.security.JwtAuthenticationRequest;
import com.user.secrets.service.UserServiceImpl;
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthenticationRestControllerTest extends SecretServiceApplicationTests {
	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");
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
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		testUtil = new TestUtil(getMvc(), userServiceImpl);
	}

	/****************** GET ACCESS TOKEN OPERATIONS ********************/

	@Test
	public void getAccessTokenWithoutBody() throws Exception {
		// Getting access token without providing body.
		mvc.perform(post("/oauth/token").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andDo(document("get-access-token-without-body"));
	}

	@Test
	public void getAccessTokenWithoutRegisteration() throws Exception {
		// Getting access token with out registering the user.
		mvc.perform(post("/oauth/token").contentType(MediaType.APPLICATION_JSON)
			.content(new Gson().toJson(new JwtAuthenticationRequest(RandomStringUtils.randomAlphanumeric(5),
					RandomStringUtils.randomAlphanumeric(5)))))
			.andExpect(status().isNotFound());
	}

	@Test
	public void getAccessTokenWithoutUsernamePassword() throws Exception {
		// Getting access token without any username, and password.
		mvc.perform(post("/oauth/token").contentType(MediaType.APPLICATION_JSON)
			.content(new Gson().toJson(new JwtAuthenticationRequest(null, null))))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void getAccessTokenWithUsernamePassword() throws Exception {
		// create new user.
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
	public void getAccessTokenWithUserDeletedAfterRegistration() throws Exception {
		// Getting access token after the user is deleted gives not authorized.
		// create user.
		User user = testUtil.createNewUser();

		// persist the user.
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

	/****************** GET REFRESH TOKEN OPERATIONS ********************/
	@Test
	public void getRefreshTokenWithoutBody() throws Exception {
		// Getting refresh token without providing body.
		mvc.perform(get("/oauth/refresh"))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void getRefreshTokenWithoutAccessToken() throws Exception {
		// Getting refresh token without providing Token.
		mvc.perform(get("/oauth/refresh"))
			.andExpect(status().isBadRequest())
			.andExpect(content().string("no token provided."));
	}

	@Test
	public void getRefreshTokenWithAccessToken() throws Exception {
		// Getting refresh token by providing a valid access token already
		// generated.

		// create user
		User user = testUtil.createNewUser();

		// persist user and get access token.
		String token = testUtil.PersistUserAndGetAccessToken(user);
		String username = user.getUsername();

		// perform get operation to get refresh token after providing access
		// token.
		mvc.perform(get("/oauth/refresh").header("Authorization", token))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("token", is(notNullValue())))
			.andExpect(jsonPath("userName", is(username)))
			.andExpect(jsonPath("validity", is(notNullValue())));
	}

	@Test
	public void getRefreshTokenWithAccessTokenAfterUserDeleted() throws Exception {
		// Getting refresh token by providing a valid access token already
		// generated but the user deleted.

		// create new user.
		User user = testUtil.createNewUser();

		// persist and get access token for the user.
		String token = testUtil.PersistUserAndGetAccessToken(user);
		String username = user.getUsername();

		// delete user.
		userServiceImpl.delete(userServiceImpl.findByUserName(username)
			.getId());

		// perform get refresh token operation after the user is deleted.
		mvc.perform(get("/oauth/refresh").header("Authorization", token))
			.andExpect(status().isNotFound())
			.andExpect(content().string("user with provided username does not exist"));
	}
}
