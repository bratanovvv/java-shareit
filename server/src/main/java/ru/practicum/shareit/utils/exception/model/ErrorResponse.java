package ru.practicum.shareit.utils.exception.model;

import java.util.List;

public record ErrorResponse(ErrorCode code, String error, List<String> details) {

	public static ErrorResponse of(ErrorCode code, String error) {
		return new ErrorResponse(code, error, List.of());
	}

	public static ErrorResponse of(ErrorCode code, String error, List<String> details) {
		return new ErrorResponse(code, error, details);
	}
}