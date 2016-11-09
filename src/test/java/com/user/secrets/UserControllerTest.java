package com.user.secrets;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.user.secrets.domain.User;
import com.user.secrets.service.UserServiceImpl;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {
	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");
	@Autowired
	WebApplicationContext context;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	UserServiceImpl userServiceImpl;

	private MockMvc mvc;

	private TestUtil testUtil;

	@Bean
	MockMvc getMvc() {
		return mvc;
	}

	@Before
	public void setUp() {

		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.webAppContextSetup(context)
			.addFilter(springSecurityFilterChain)
			.apply(documentationConfiguration(this.restDocumentation).uris()
					.withScheme("https")
					.withHost("${app.name}")
					.withPort(443))
			.build();
		testUtil = new TestUtil(getMvc(), userServiceImpl);
	}

	@Test
	public void getPutDeleteWithoutAuthorization() throws Exception {
		// GET, PUT, DELETE without any authorization.
		URI uri = new URI("/user");
		URI uri1 = new URI("/user" + (0 + (int) (Math.random() * 1000)));
		mvc.perform(get(uri))
			.andExpect(status().isUnauthorized())
			.andDo(document("get-user-without-authorization", preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint())));
		mvc.perform(put(uri1))
			.andExpect(status().isUnauthorized())
			.andDo(document("update-user-without-authorization", preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint())));
		mvc.perform(delete(uri1))
			.andExpect(status().isUnauthorized())
			.andDo(document("delete-user-without-authorization", preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint())));
	}

	/************************* CREATE OPERATIONS **************************/
	@Test
	public void createUser() throws Exception {

		mvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
			.content(new Gson().toJson(testUtil.createNewUser())))
			.andExpect(status().isCreated())
			.andDo(document("create-user", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
					requestFields(testUtil.getUserRequestFieldDescriptor()),
					responseFields(testUtil.getUserResponseFieldDescriptor())));
	}

	@Test
	public void createDuplicateUser() throws Exception {
		User user = testUtil.createNewUser();
		testUtil.persistUser(user);
		mvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
			.content(new Gson().toJson(user)))
			.andExpect(status().isConflict())
			.andExpect(content().string("user already exists: " + user.getUsername()));
	}

	/************************* UPDATE OPERATIONS **************************/
	@Test
	public void updateUserwithoutAuthorization() throws Exception {
		// update user without access token.
		User user = testUtil.createNewUser();
		MvcResult mockResult = testUtil.persistUser(user);
		JsonObject jsonObject = testUtil.getJsonFromResult(mockResult);

		// update user fields.
		user.setFirstName("ayush");
		user.setLastName("choukse");
		user.setEmail("ayush.choukse@gmail.com");

		mvc.perform(put("/user" + jsonObject.get("id")
			.getAsString()).contentType(MediaType.APPLICATION_JSON)
				.content(new Gson().toJson(user)))
			.andExpect(status().isUnauthorized())
			.andDo(document("update-user-without-authorization", preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint())));
	}

	@Test
	public void updateUserwithAuthorization() throws Exception {
		// update user with access token.
		User user = testUtil.createNewUser();

		String accessToken = testUtil.PersistUserAndGetAccessToken(user);
		Long userId = testUtil.getUserId(user);
		// update user.
		user.setFirstName("ayush");
		user.setLastName("choukse");
		user.setEmail("ayush.choukse@gmail.com");

		// perform put operation.
		mvc.perform(put("/user/" + userId).contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", accessToken)
			.content(new Gson().toJson(user)))
			.andExpect(status().isOk())
			.andDo(document("update-user-with-authorization", preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestHeaders(headerWithName("Authorization").description("Access token for the request.")),
					requestFields(testUtil.getUserRequestFieldDescriptor()),
					responseFields(testUtil.getUserResponseFieldDescriptor())));
	}

	@Test
	public void updateAnotherUserwithAuthorization() throws Exception {
		// update user with access token.

		User user1 = testUtil.createNewUser();
		User user2 = testUtil.createNewUser();

		// persist user2.
		testUtil.persistUser(user2);

		// get access token for user1.
		String accessToken = testUtil.PersistUserAndGetAccessToken(user1);

		// update user2 fields.
		user2.setFirstName("ayush");
		user2.setLastName("choukse");
		user2.setEmail("ayush.choukse@gmail.com");
		Long user2Id = testUtil.getUserId(user2);

		// perform update operation on user2 with access token from user1.
		mvc.perform(put("/user/" + user2Id).contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", accessToken)
			.content(new Gson().toJson(user2)))
			.andExpect(status().isForbidden())
			.andDo(document("update-another-user-with-authorization", preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestHeaders(headerWithName("Authorization").description(accessToken))));
	}

	/************************* DELETE OPERATIONS **************************/
	@Test
	public void deleteUserWithAuthorization() throws Exception {
		// get user.
		User user = testUtil.createNewUser();

		// get token string.
		String token = testUtil.PersistUserAndGetAccessToken(user);

		Long userId = testUtil.getUserId(user);

		// perform delete operation.
		mvc.perform(delete("/user/" + userId).header("Authorization", token))
			.andExpect(status().isOk())
			.andExpect(content().json(testUtil.getString("user deleted: " + userId)))
			.andDo(document("delete-user-with-authorization",
					requestHeaders(headerWithName("Authorization").description("Access token for the request.")),
					responseFields(fieldWithPath("message").description("Response from the request."))));
	}

	@Test
	public void deleteAnotherUserWithAuthorization() throws Exception {

		User user1 = testUtil.createNewUser();
		String user1Token = testUtil.PersistUserAndGetAccessToken(user1);

		User user2 = testUtil.createNewUser();
		testUtil.PersistUserAndGetAccessToken(user2);

		Long user2Id = testUtil.getUserId(user2);

		// perform delete operation on user2 with access token from user1.
		mvc.perform(delete("/user/" + user2Id).header("Authorization", user1Token))
			.andExpect(status().isForbidden())
			.andDo(document("delete-another-user-with-authorization", preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestHeaders(headerWithName("Authorization").description(user1Token))));
	}

	/************************* GET OPERATIONS **************************/
	@Test
	public void getUserWithAuthorization() throws Exception {
		User user = testUtil.createNewUser();
		String token = testUtil.PersistUserAndGetAccessToken(user);
		Long userId = testUtil.getUserId(user);

		// perform get operation for user.
		mvc.perform(get("/user/" + userId).header("Authorization", token))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("id", is(notNullValue())))
			.andExpect(jsonPath("username", is(user.getUsername())))
			.andExpect(jsonPath("email", is(user.getEmail())))
			.andExpect(jsonPath("firstName", is(user.getFirstName())))
			.andExpect(jsonPath("lastName", is(user.getLastName())))
			.andExpect(jsonPath("enabled", is(notNullValue())))
			.andExpect(jsonPath("lastPasswordResetDate", is(notNullValue())))
			.andDo(document("get-user-with-authorization", preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestHeaders(headerWithName("Authorization").description(token)),
					responseFields(testUtil.getUserResponseFieldDescriptor())));
	}

	@Test
	public void getAnotherUserWithAuthorization() throws Exception {
		User user1 = testUtil.createNewUser();

		User user2 = testUtil.createNewUser();
		testUtil.persistUser(user2);

		Long user2Id = testUtil.getUserId(user2);

		String user1Token = testUtil.PersistUserAndGetAccessToken(user1);

		// perform get operation on user2 with access token of user1.
		mvc.perform(get("/user/" + user2Id).header("Authorization", user1Token))
			.andExpect(status().isForbidden())
			.andDo(document("get-another-user-with-authorization", preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestHeaders(headerWithName("Authorization").description(user1Token))));
	}

	@Test
	public void getUserAfterUserDeletedWithAuthorization() throws Exception {
		User user = testUtil.createNewUser();
		String token = testUtil.PersistUserAndGetAccessToken(user);
		Long userId = testUtil.getUserId(user);
		userServiceImpl.delete(userId);

		// perform get operation on user with access token after the user is
		// deleted.
		mvc.perform(get("/user/" + userId).header("Authorization", token))
			.andExpect(status().isUnauthorized())
			.andDo(document("get-user-after-user-deleted-with-authorization", preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestHeaders(headerWithName("Authorization").description(token))));
	}
}
