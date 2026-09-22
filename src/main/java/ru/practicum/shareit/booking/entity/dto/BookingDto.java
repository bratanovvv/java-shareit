package ru.practicum.shareit.booking.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.entity.model.BookingStatus;
import ru.practicum.shareit.item.entity.dto.ItemDto;
import ru.practicum.shareit.user.entity.dto.UserDto;
import ru.practicum.shareit.utils.validation.HasPeriod;
import ru.practicum.shareit.utils.validation.annotation.StartBeforeEnd;
import ru.practicum.shareit.utils.validation.group.Create;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEnd(groups = Create.class, message = "Booking end must be after start")
public class BookingDto implements HasPeriod {

	private Long id;

	@NotNull(groups = Create.class, message = "Booking start is required")
	private LocalDateTime start;

	@NotNull(groups = Create.class, message = "Booking end is required")
	private LocalDateTime end;

	@NotNull(groups = Create.class, message = "Booked item id is required")
	private Long itemId;

	private ItemDto item;

	private UserDto booker;

	private BookingStatus status;
}
