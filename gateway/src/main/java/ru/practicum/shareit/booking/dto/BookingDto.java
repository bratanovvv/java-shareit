package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

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
