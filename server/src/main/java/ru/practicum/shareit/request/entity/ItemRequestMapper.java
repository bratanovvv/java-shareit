package ru.practicum.shareit.request.entity;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.request.entity.dto.ItemRequestDto;
import ru.practicum.shareit.request.entity.dto.RequestAnswerDto;
import ru.practicum.shareit.request.entity.model.ItemRequest;
import ru.practicum.shareit.utils.mapper.Mapper;

import java.util.List;

@Component
public final class ItemRequestMapper implements Mapper<ItemRequest, ItemRequestDto> {

	@Override
	public ItemRequestDto toDto(ItemRequest request) {
		return new ItemRequestDto(
				request.getId(),
				request.getDescription(),
				request.getCreated(),
				toAnswerDtos(request.getItems())
		);
	}

	@Override
	public ItemRequest toEntity(ItemRequestDto requestDto) {
		return new ItemRequest(
				requestDto.getId(),
				requestDto.getDescription(),
				null,
				null,
				null
		);
	}

	private List<RequestAnswerDto> toAnswerDtos(List<Item> items) {
		if (items == null) {
			return List.of();
		}
		return items.stream().map(this::toAnswerDto).toList();
	}

	private RequestAnswerDto toAnswerDto(Item item) {
		return new RequestAnswerDto(
				item.getId(),
				item.getName(),
				item.getOwner() != null ? item.getOwner().getId() : null
		);
	}
}
