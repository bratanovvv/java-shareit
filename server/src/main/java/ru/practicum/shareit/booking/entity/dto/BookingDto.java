package ru.practicum.shareit.booking.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.entity.model.BookingStatus;
import ru.practicum.shareit.item.entity.dto.ItemDto;
import ru.practicum.shareit.user.entity.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

	private Long id;

	private LocalDateTime start;

	private LocalDateTime end;

	private Long itemId;

	private ItemDto item;

	private UserDto booker;

	private BookingStatus status;
}
