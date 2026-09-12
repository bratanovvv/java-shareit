package ru.practicum.shareit.utils.exception.errors.impl;

import ru.practicum.shareit.utils.exception.errors.ShareitException;

public class ForbiddenException extends ShareitException {

	public ForbiddenException(String message) {
		super(message);
	}
}
