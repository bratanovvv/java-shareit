package ru.practicum.shareit.utils.exception.impl;

import ru.practicum.shareit.utils.exception.ShareitException;

public class NotFoundException extends ShareitException {

	public NotFoundException(String message) {
		super(message);
	}
}
