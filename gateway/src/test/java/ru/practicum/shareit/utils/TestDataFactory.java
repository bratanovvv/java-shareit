package ru.practicum.shareit.utils;

import java.time.LocalDateTime;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

public final class TestDataFactory {

	private TestDataFactory() {
	}

	public static UserDto userDto(Long id, String name, String email) {
		return new UserDto(id, name, email);
	}

	public static ItemDto itemDto(Long id, String name, String description, Boolean available, Long requestId) {
		return new ItemDto(id, name, description, available, requestId, null, null, null);
	}

	public static CommentDto commentDto(String text) {
		return new CommentDto(null, text, null, null);
	}

	public static ItemRequestDto itemRequestDto(String description) {
		return new ItemRequestDto(null, description, null, null);
	}

	public static BookItemRequestDto bookItemRequestDto(Long itemId, LocalDateTime start, LocalDateTime end) {
		return new BookItemRequestDto(itemId, start, end);
	}
}
