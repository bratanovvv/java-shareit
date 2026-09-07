package ru.practicum.shareit.item.entity;

import ru.practicum.shareit.item.entity.dto.ItemDto;
import ru.practicum.shareit.item.entity.model.Item;

public final class ItemMapper {

	private ItemMapper() {
	}

	public static ItemDto toItemDto(Item item) {
		return new ItemDto(
				item.getId(),
				item.getName(),
				item.getDescription(),
				item.getAvailable(),
				item.getRequest() != null ? item.getRequest().getId() : null
		);
	}

	public static Item toItem(ItemDto itemDto) {
		return new Item(
				itemDto.getId(),
				itemDto.getName(),
				itemDto.getDescription(),
				itemDto.getAvailable(),
				null,
				null
		);
	}
}
