package ru.practicum.shareit;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BaseClientTest {

	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	private RestTemplate rest;
	private MockRestServiceServer server;
	private BaseClient client;

	@BeforeEach
	void setUp() {
		rest = new RestTemplateBuilder()
				.uriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:9090"))
				.build();
		server = MockRestServiceServer.bindTo(rest).build();
		client = new BaseClient(rest);
	}

	@Test
	void shouldPassThroughServerErrorWithJsonContentType() {
		String body = "{\"code\":\"CONFLICT\",\"error\":\"Email is already in use\"}";
		server.expect(requestTo("http://localhost:9090/users"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.CONFLICT)
						.contentType(MediaType.APPLICATION_JSON)
						.body(body));

		ResponseEntity<Object> response = client.get("/users");

		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		assertEquals(body, new String((byte[]) response.getBody(), StandardCharsets.UTF_8));
		server.verify();
	}

	@Test
	void shouldPassThroughServerSuccessResponse() {
		String body = "{\"id\":1,\"name\":\"user\"}";
		server.expect(requestTo("http://localhost:9090/users"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

		ResponseEntity<Object> response = client.get("/users");

		assertEquals(HttpStatus.OK, response.getStatusCode());
		server.verify();
	}

	@Test
	void getWithParametersShouldExpandUriAndSetHeader() {
		server.expect(requestTo("http://localhost:9090/bookings?state=ALL"))
				.andExpect(method(HttpMethod.GET))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

		client.get("/bookings?state={state}", 1L, Map.of("state", "ALL"));

		server.verify();
	}

	@Test
	void getWithUserIdShouldSetHeader() {
		server.expect(requestTo("http://localhost:9090/users"))
				.andExpect(method(HttpMethod.GET))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

		client.get("/users", 1L);

		server.verify();
	}

	@Test
	void postShouldSetUserIdHeader() {
		server.expect(requestTo("http://localhost:9090/items"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

		client.post("/items", 1L, Map.of("name", "Drill"));

		server.verify();
	}

	@Test
	void postWithoutUserIdShouldSendRequest() {
		server.expect(requestTo("http://localhost:9090/items"))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

		client.post("/items", Map.of("name", "Drill"));

		server.verify();
	}

	@Test
	void putShouldSendRequest() {
		server.expect(requestTo("http://localhost:9090/items/1"))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

		client.put("/items/1", 1L, Map.of("name", "Drill"));

		server.verify();
	}

	@Test
	void patchShouldSendRequest() {
		server.expect(requestTo("http://localhost:9090/items/1"))
				.andExpect(method(HttpMethod.PATCH))
				.andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

		client.patch("/items/1", 1L, Map.of("name", "Drill"));

		server.verify();
	}

	@Test
	void patchWithoutUserIdShouldSendRequest() {
		server.expect(requestTo("http://localhost:9090/items/1"))
				.andExpect(method(HttpMethod.PATCH))
				.andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

		client.patch("/items/1", Map.of("name", "Drill"));

		server.verify();
	}

	@Test
	void patchWithUserIdOnlyShouldSendRequest() {
		server.expect(requestTo("http://localhost:9090/items/1"))
				.andExpect(method(HttpMethod.PATCH))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

		client.patch("/items/1", 1L);

		server.verify();
	}

	@Test
	void deleteShouldSendRequest() {
		server.expect(requestTo("http://localhost:9090/users/1"))
				.andExpect(method(HttpMethod.DELETE))
				.andRespond(withSuccess());

		client.delete("/users/1");

		server.verify();
	}

	@Test
	void deleteWithUserIdShouldSetHeader() {
		server.expect(requestTo("http://localhost:9090/users/1"))
				.andExpect(method(HttpMethod.DELETE))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess());

		client.delete("/users/1", 1L);

		server.verify();
	}
}
