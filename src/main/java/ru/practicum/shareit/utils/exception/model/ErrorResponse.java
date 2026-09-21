package ru.practicum.shareit.utils.exception.model;

import java.util.List;

public record ErrorResponse(ErrorCode code, String message, List<String> details) {

	public static ErrorResponse of(ErrorCode code, String message) {
		return new ErrorResponse(code, message, List.of());
	}

	public static ErrorResponse of(ErrorCode code, String message, List<String> details) {
		return new ErrorResponse(code, message, details);
	}
}