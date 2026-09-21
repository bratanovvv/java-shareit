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
import ru.practicum.shareit.utils.exception.model.ErrorCode;
import ru.practicum.shareit.utils.exception.model.ErrorResponse;
import ru.practicum.shareit.utils.exception.errors.ShareitException;
import ru.practicum.shareit.utils.exception.errors.impl.ConflictException;
import ru.practicum.shareit.utils.exception.errors.impl.ForbiddenException;
import ru.practicum.shareit.utils.exception.errors.impl.NotFoundException;
import ru.practicum.shareit.utils.exception.errors.impl.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException exception) {
		log.warn("Entity not found: {}", exception.getMessage());
		return buildResponse(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleConflict(ConflictException exception) {
		log.warn("Conflict: {}", exception.getMessage());
		return buildResponse(HttpStatus.CONFLICT, ErrorCode.CONFLICT, exception.getMessage());
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException exception) {
		log.warn("Access denied: {}", exception.getMessage());
		return buildResponse(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, exception.getMessage());
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleValidation(ValidationException exception) {
		log.warn("Validation failed: {}", exception.getMessage());
		return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR, exception.getMessage());
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
		List<String> details = exception.getBindingResult().getFieldErrors().stream()
				.map(this::formatFieldError)
				.collect(Collectors.toList());
		log.warn("Request body validation failed: {}", details);
		return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR,
				"Request body validation failed", details);
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleBindingError(ServletRequestBindingException exception) {
		log.warn("Request binding failed: {}", exception.getMessage());
		return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
				"Required request parameter or header is missing");
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
		log.warn("Request value type mismatch: {}", exception.getMessage());
		return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, "Invalid request value format");
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException exception) {
		log.warn("Request body is not readable: {}", exception.getMessage());
		return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, "Request body is malformed");
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException exception) {
		return buildResponse(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Resource not found");
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleShareitException(ShareitException exception) {
		log.error("Unexpected application error", exception);
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR, exception.getMessage());
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
		log.error("Unexpected error", exception);
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR, exception.getMessage());
	}

	private String formatFieldError(FieldError error) {
		return error.getField() + ": " + error.getDefaultMessage();
	}

	private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, ErrorCode code, String message) {
		return ResponseEntity.status(status).body(ErrorResponse.of(code, message));
	}

	private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status,
														ErrorCode code,
														String message,
														List<String> details) {
		return ResponseEntity.status(status).body(ErrorResponse.of(code, message, details));
	}
}