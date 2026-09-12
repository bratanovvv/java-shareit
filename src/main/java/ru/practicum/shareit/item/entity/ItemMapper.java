package ru.practicum.shareit.item.entity;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.entity.dto.ItemDto;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.utils.mapper.Mapper;

@Component
public final class ItemMapper implements Mapper<Item, ItemDto> {

	@Override
	public ItemDto toDto(Item item) {
		return new ItemDto(
				item.getId(),
				item.getName(),
				item.getDescription(),
				item.getAvailable(),
				item.getRequest() != null ? item.getRequest().getId() : null
		);
	}

	@Override
	public Item toEntity(ItemDto itemDto) {
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