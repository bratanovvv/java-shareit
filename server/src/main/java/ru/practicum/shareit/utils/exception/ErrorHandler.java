package ru.practicum.shareit.utils.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.practicum.shareit.utils.exception.model.ErrorCode;
import ru.practicum.shareit.utils.exception.model.ErrorResponse;
import ru.practicum.shareit.utils.exception.errors.impl.BusinessException;
import ru.practicum.shareit.utils.exception.errors.impl.ConflictException;
import ru.practicum.shareit.utils.exception.errors.impl.ForbiddenException;
import ru.practicum.shareit.utils.exception.errors.impl.NotFoundException;

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
	public ResponseEntity<ErrorResponse> handleBusiness(BusinessException exception) {
		log.warn("Business rule violated: {}", exception.getMessage());
		return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, exception.getMessage());
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
	public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
		log.error("Unexpected error", exception);
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR, exception.getMessage());
	}

	private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, ErrorCode code, String error) {
		return ResponseEntity.status(status).body(ErrorResponse.of(code, error));
	}
}