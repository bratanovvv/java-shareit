package ru.practicum.shareit.booking.entity;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.entity.dto.BookingDto;
import ru.practicum.shareit.booking.entity.model.Booking;
import ru.practicum.shareit.item.entity.ItemMapper;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.user.entity.UserMapper;
import ru.practicum.shareit.utils.mapper.Mapper;

@Component
public final class BookingMapper implements Mapper<Booking, BookingDto> {

	private final ItemMapper itemMapper;
	private final UserMapper userMapper;

	public BookingMapper(ItemMapper itemMapper, UserMapper userMapper) {
		this.itemMapper = itemMapper;
		this.userMapper = userMapper;
	}

	@Override
	public BookingDto toDto(Booking booking) {
		return new BookingDto(
				booking.getId(),
				booking.getStart(),
				booking.getEnd(),
				booking.getItem() != null ? booking.getItem().getId() : null,
				booking.getItem() != null ? itemMapper.toDto(booking.getItem()) : null,
				booking.getBooker() != null ? userMapper.toDto(booking.getBooker()) : null,
				booking.getStatus()
		);
	}

	@Override
	public Booking toEntity(BookingDto bookingDto) {
		Item item = null;
		if (bookingDto.getItemId() != null) {
			item = new Item();
			item.setId(bookingDto.getItemId());
		}
		return new Booking(
				bookingDto.getId(),
				bookingDto.getStart(),
				bookingDto.getEnd(),
				item,
				null,
				null
		);
	}
}
