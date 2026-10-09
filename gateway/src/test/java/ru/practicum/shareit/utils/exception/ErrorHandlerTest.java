package ru.practicum.shareit.utils.exception;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.practicum.shareit.utils.exception.model.ErrorCode;
import ru.practicum.shareit.utils.exception.model.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorHandlerTest {

	private final ErrorHandler handler = new ErrorHandler();
	private final ObjectMapper mapper = new ObjectMapper();

	@Test
	void shouldReturnStructuredBadRequestForIllegalArgument() {
		ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(new IllegalArgumentException("Unknown state: FOO"));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(ErrorCode.BAD_REQUEST, response.getBody().code());
		assertEquals("Unknown state: FOO", response.getBody().error());
	}

	@Test
	void shouldReturnStructuredInternalErrorForUnexpected() {
		ResponseEntity<ErrorResponse> response = handler.handleUnexpected(new RuntimeException("boom"));

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals(ErrorCode.INTERNAL_ERROR, response.getBody().code());
		assertEquals("Internal server error", response.getBody().error());
	}

	@Test
	void shouldReturnStructuredNotFoundForMissingResource() {
		ResponseEntity<ErrorResponse> response =
				handler.handleNoResourceFound(new NoResourceFoundException(HttpMethod.GET, "/unknown"));

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals(ErrorCode.NOT_FOUND, response.getBody().code());
	}

	@Test
	void shouldSerializeWithErrorField() throws Exception {
		ErrorResponse response = ErrorResponse.of(ErrorCode.VALIDATION_ERROR,
				"Request body validation failed", List.of("name: Item name is required"));

		String json = mapper.writeValueAsString(response);

		assertTrue(json.contains("\"error\""));
		assertTrue(json.contains("\"code\""));
		assertTrue(json.contains("\"details\""));
		assertFalse(json.contains("\"message\""));
	}
}
