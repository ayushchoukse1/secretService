package com.user.secrets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.user.secrets.domain.Secret;
import com.user.secrets.domain.User;
import com.user.secrets.service.SecretServiceImpl;
import com.user.secrets.service.UserServiceImpl;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class SecretControllerTest {
	@Autowired
	WebApplicationContext context;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	UserServiceImpl userServiceImpl;

	@Autowired
	SecretServiceImpl secretServiceImpl;

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
		testUtil.setSecretServiceImpl(secretServiceImpl);
	}

	// @Test
	public void getPostPutDeleteSecretWithoutAuthorization() throws Exception {
		mvc.perform(post("/secret").contentType(MediaType.APPLICATION_JSON)
			.content(new Gson().toJson(testUtil.createNewSecret())))
			.andExpect(status().isUnauthorized());
		mvc.perform(get("/secret/"))
			.andExpect(status().isUnauthorized());
		mvc.perform(delete("/secret/"))
			.andExpect(status().isUnauthorized());
		mvc.perform(get("/secrets"))
			.andExpect(status().isUnauthorized());
		mvc.perform(put("/secret/").contentType(MediaType.APPLICATION_JSON)
			.content(new Gson().toJson(testUtil.createNewSecret())))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void createSecretWithAuthorization() throws Exception {
		String token = testUtil.PersistUserAndGetAccessToken(testUtil.createNewUser());
		mvc.perform(post("/secret").contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", token)
			.content(new Gson().toJson(testUtil.createNewSecret())))
			.andExpect(status().isCreated());
	}

	@Test
	public void updateSecretWithAuthorization() throws Exception {
		String token = testUtil.PersistUserAndGetAccessToken(testUtil.createNewUser());
		Secret secret = testUtil.createNewSecret();
		MvcResult result = testUtil.persistSecretWithAuthorization(secret, token);
		JsonObject object = testUtil.getJsonFromResult(result);
		Long secretId = object.get("id")
			.getAsLong();
		String newTitle = "Changed title";
		String newBody = "Hello its the changed body.";
		secret.setBody(newBody);
		secret.setTitle(newTitle);

		mvc.perform(put("/secret/" + secretId).contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", token)
			.content(new Gson().toJson(secret)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().json("{'id':" + secretId + ",'body':" + newBody + ",'title':" + newTitle + "}"));
	}

	@Test
	public void updateAnotherUserSecretWithAuthorization() throws Exception {
		// creating,persisting user1.
		User user1 = testUtil.createNewUser();
		String user1Token = testUtil.PersistUserAndGetAccessToken(user1);
		Secret user1Secret1 = testUtil.createNewSecret();

		// persisting user1Secret1 using user1 token1Token.
		MvcResult result = testUtil.persistSecretWithAuthorization(user1Secret1, user1Token);
		JsonObject object = testUtil.getJsonFromResult(result);
		Long user1Secret1Id = object.get("id")
			.getAsLong();
		user1Secret1.setTitle("Title changed by user2.");
		user1Secret1.setBody("Body changed by user2.");

		// creating user2 and getting access token.
		User user2 = testUtil.createNewUser();
		String user2Token = testUtil.PersistUserAndGetAccessToken(user2);

		// perform update operation on user1Secret1 by using access token from
		// user2.
		mvc.perform(put("/secret/" + user1Secret1Id).contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", user2Token)
			.content(new Gson().toJson(user1Secret1)))
			.andExpect(status().isForbidden());
	}

	@Test
	public void deleteSecretWithAuthorization() throws Exception {
		// create and persist new user.
		String token = testUtil.PersistUserAndGetAccessToken(testUtil.createNewUser());
		// create and persist new secret.
		MvcResult result = testUtil.persistSecretWithAuthorization(testUtil.createNewSecret(), token);
		JsonObject object = testUtil.getJsonFromResult(result);
		Long secretId = object.get("id")
			.getAsLong();
		// perform delete operation.
		mvc.perform(delete("/secret/" + secretId).header("Authorization", token))
			.andExpect(status().isOk());
	}

	@Test
	public void deleteAnotherUserSecretWithAuthorization() throws Exception {
		// creating,persisting user1.
		User user1 = testUtil.createNewUser();
		String user1Token = testUtil.PersistUserAndGetAccessToken(user1);
		Secret user1Secret1 = testUtil.createNewSecret();

		// persisting user1Secret1 using user1 token1Token.
		MvcResult result = testUtil.persistSecretWithAuthorization(user1Secret1, user1Token);
		JsonObject object = testUtil.getJsonFromResult(result);
		Long user1Secret1Id = object.get("id")
			.getAsLong();

		// creating user2 and getting access token.
		User user2 = testUtil.createNewUser();
		String user2Token = testUtil.PersistUserAndGetAccessToken(user2);

		// perform delete operation on user1Secret1 by using access token from
		// user2.
		mvc.perform(delete("/secret/" + user1Secret1Id).header("Authorization", user2Token))
			.andExpect(status().isForbidden());
	}

	@Test
	public void deleteNonExistingSecretWithAuthorization() throws Exception {
		String token = testUtil.PersistUserAndGetAccessToken(testUtil.createNewUser());

		// perform delete operation on a random non existing secret.
		mvc.perform(delete("/secret/" + (25 + (int) (Math.random() * 1000))).header("Authorization", token))
			.andExpect(status().isForbidden());
	}

	@Test
	public void getSecretWithAuthorization() throws Exception {
		User user = testUtil.createNewUser();
		String token = testUtil.PersistUserAndGetAccessToken(user);
		Secret secret = testUtil.createNewSecret();
		MvcResult result = testUtil.persistSecretWithAuthorization(secret, token);
		JsonObject object = testUtil.getJsonFromResult(result);
		Long secretId = object.get("id")
			.getAsLong();

		mvc.perform(get("/secret/" + secretId).header("Authorization", token))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
	}

}
