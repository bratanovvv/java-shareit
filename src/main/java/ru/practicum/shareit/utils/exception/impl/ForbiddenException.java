package ru.practicum.shareit.utils.exception.impl;

import ru.practicum.shareit.utils.exception.ShareitException;

public class ForbiddenException extends ShareitException {

	public ForbiddenException(String message) {
		super(message);
	}
}
