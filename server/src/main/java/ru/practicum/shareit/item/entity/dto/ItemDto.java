package ru.practicum.shareit.item.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.entity.dto.BookingInfoDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

	private Long id;

	private String name;

	private String description;

	private Boolean available;

	private Long requestId;

	private BookingInfoDto lastBooking;

	private BookingInfoDto nextBooking;

	private List<CommentDto> comments;
}
