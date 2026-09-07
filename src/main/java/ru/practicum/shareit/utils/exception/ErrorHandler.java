package ru.practicum.shareit.utils.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.practicum.shareit.utils.exception.impl.ConflictException;
import ru.practicum.shareit.utils.exception.impl.ForbiddenException;
import ru.practicum.shareit.utils.exception.impl.NotFoundException;
import ru.practicum.shareit.utils.exception.impl.ValidationException;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler
	public ResponseEntity<Map<String, String>> handleNotFound(NotFoundException exception) {
		log.warn("Entity not found: {}", exception.getMessage());
		return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, String>> handleConflict(ConflictException exception) {
		log.warn("Conflict: {}", exception.getMessage());
		return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, String>> handleForbidden(ForbiddenException exception) {
		log.warn("Access denied: {}", exception.getMessage());
		return buildResponse(HttpStatus.FORBIDDEN, exception.getMessage());
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, String>> handleValidation(ValidationException exception) {
		log.warn("Validation failed: {}", exception.getMessage());
		return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
		String message = exception.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining("; "));
		log.warn("Request body validation failed: {}", message);
		return buildResponse(HttpStatus.BAD_REQUEST, message);
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, String>> handleBindingError(ServletRequestBindingException exception) {
		log.warn("Request binding failed: {}", exception.getMessage());
		return buildResponse(HttpStatus.BAD_REQUEST, "Required request parameter or header is missing");
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
		log.warn("Request value type mismatch: {}", exception.getMessage());
		return buildResponse(HttpStatus.BAD_REQUEST, "Invalid request value format");
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, String>> handleUnreadableMessage(HttpMessageNotReadableException exception) {
		log.warn("Request body is not readable: {}", exception.getMessage());
		return buildResponse(HttpStatus.BAD_REQUEST, "Request body is malformed");
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, String>> handleNoResourceFound(NoResourceFoundException exception) {
		return buildResponse(HttpStatus.NOT_FOUND, "Resource not found");
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, String>> handleShareitException(ShareitException exception) {
		log.error("Unexpected application error", exception);
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, String>> handleUnexpected(Exception exception) {
		log.error("Unexpected error", exception);
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
	}

	private ResponseEntity<Map<String, String>> buildResponse(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(Map.of("error", message));
	}
}
