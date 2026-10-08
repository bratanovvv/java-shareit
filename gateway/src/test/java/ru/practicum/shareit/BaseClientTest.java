package ru.practicum.shareit;

import java.nio.charset.StandardCharsets;

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
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BaseClientTest {

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
}
