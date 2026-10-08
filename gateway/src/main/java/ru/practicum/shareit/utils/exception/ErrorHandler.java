package ru.practicum.shareit.utils.exception;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.practicum.shareit.utils.exception.model.ErrorCode;
import ru.practicum.shareit.utils.exception.model.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
		List<String> details = exception.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.collect(Collectors.toList());
		log.warn("Request body validation failed: {}", details);
		return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR,
				"Request body validation failed", details);
	}

	@ExceptionHandler(ServletRequestBindingException.class)
	public ResponseEntity<ErrorResponse> handleBinding(ServletRequestBindingException exception) {
		log.warn("Request binding failed: {}", exception.getMessage());
		return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
				"Required request parameter or header is missing");
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
		log.warn("Request value type mismatch: {}", exception.getMessage());
		return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, "Invalid request value format");
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException exception) {
		log.warn("Request body is not readable: {}", exception.getMessage());
		return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, "Request body is malformed");
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
		log.warn("Illegal argument: {}", exception.getMessage());
		return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException exception) {
		return buildResponse(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Resource not found");
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
		log.error("Unexpected error", exception);
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR, "Internal server error");
	}

	private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, ErrorCode code, String error) {
		return ResponseEntity.status(status).body(ErrorResponse.of(code, error));
	}

	private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, ErrorCode code, String error,
														List<String> details) {
		return ResponseEntity.status(status).body(ErrorResponse.of(code, error, details));
	}
}
