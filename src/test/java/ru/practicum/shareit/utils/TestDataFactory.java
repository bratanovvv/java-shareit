package ru.practicum.shareit.utils;

import ru.practicum.shareit.item.entity.model.Item;
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

	public static Item item() {
		return item("Item", "Item description", true);
	}

	public static Item item(String name, String description, Boolean available) {
		return new Item(null, name, description, available, null, null);
	}
}