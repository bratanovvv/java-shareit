package ru.practicum.shareit.item.entity;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.entity.dto.BookingInfoDto;
import ru.practicum.shareit.booking.entity.model.Booking;
import ru.practicum.shareit.item.entity.dto.CommentDto;
import ru.practicum.shareit.item.entity.dto.ItemDto;
import ru.practicum.shareit.item.entity.model.Comment;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.utils.mapper.Mapper;

import java.util.List;

@Component
public final class ItemMapper implements Mapper<Item, ItemDto> {

	private final Mapper<Comment, CommentDto> commentMapper;

	public ItemMapper(Mapper<Comment, CommentDto> commentMapper) {
		this.commentMapper = commentMapper;
	}

	@Override
	public ItemDto toDto(Item item) {
		return new ItemDto(
				item.getId(),
				item.getName(),
				item.getDescription(),
				item.getAvailable(),
				item.getRequest() != null ? item.getRequest().getId() : null,
				toShortDto(item.getLastBooking()),
				toShortDto(item.getNextBooking()),
				toCommentDtos(item.getComments())
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
				null,
				null,
				null,
				null
		);
	}

	private BookingInfoDto toShortDto(Booking booking) {
		if (booking == null) {
			return null;
		}
		return new BookingInfoDto(
				booking.getId(),
				booking.getBooker().getId(),
				booking.getStart(),
				booking.getEnd()
		);
	}

	private List<CommentDto> toCommentDtos(List<Comment> comments) {
		return commentMapper.toDtoList(comments);
	}
}
