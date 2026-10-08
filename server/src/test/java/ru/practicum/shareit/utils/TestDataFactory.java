package ru.practicum.shareit.utils;

import java.time.LocalDateTime;
import java.util.List;

import ru.practicum.shareit.booking.entity.model.Booking;
import ru.practicum.shareit.booking.entity.model.BookingStatus;
import ru.practicum.shareit.item.entity.model.Comment;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.request.entity.model.ItemRequest;
import ru.practicum.shareit.user.entity.model.User;

public final class TestDataFactory {

	private TestDataFactory() {
	}

	public static User user() {
		return user("user", "user@email.com");
	}

	public static User user(String name, String email) {
		return new User(null, name, email);
	}

	public static User user(long id, String name, String email) {
		return new User(id, name, email);
	}

	public static Item item() {
		return item("Item", "Item description", true);
	}

	public static Item item(String name, String description, Boolean available) {
		return new Item(null, name, description, available, null, null, null, null, null);
	}

	public static Item item(long id, String name, String description, Boolean available) {
		return new Item(id, name, description, available, null, null, null, null, null);
	}

	public static Item item(long id, String name, String description, Boolean available, User owner) {
		return new Item(id, name, description, available, owner, null, null, null, null);
	}

	public static Booking booking(long id, LocalDateTime start, LocalDateTime end, Item item, User booker,
								  BookingStatus status) {
		return new Booking(id, start, end, item, booker, status);
	}

	public static Comment comment(long id, String text, User author, LocalDateTime created) {
		return new Comment(id, text, null, author, created);
	}

	public static ItemRequest itemRequest(long id, String description, LocalDateTime created, List<Item> items) {
		return new ItemRequest(id, description, null, created, items);
	}
}
