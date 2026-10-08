package ru.practicum.shareit.utils.exception.errors.impl;

import ru.practicum.shareit.utils.exception.errors.ShareitException;

public class NotFoundException extends ShareitException {

	public NotFoundException(String message) {
		super(message);
	}
}
