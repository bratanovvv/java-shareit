package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.utils.validation.HasPeriod;
import ru.practicum.shareit.utils.validation.annotation.StartBeforeEnd;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEnd(message = "Booking end must be after start")
public class BookItemRequestDto implements HasPeriod {

	@NotNull(message = "Booked item id is required")
	private Long itemId;

	@NotNull(message = "Booking start is required")
	@FutureOrPresent
	private LocalDateTime start;

	@NotNull(message = "Booking end is required")
	@Future
	private LocalDateTime end;
}
