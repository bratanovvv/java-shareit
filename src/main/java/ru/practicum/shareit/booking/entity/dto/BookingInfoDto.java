package ru.practicum.shareit.booking.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingInfoDto {

	private Long id;

	private Long bookerId;

	private LocalDateTime start;

	private LocalDateTime end;
}
