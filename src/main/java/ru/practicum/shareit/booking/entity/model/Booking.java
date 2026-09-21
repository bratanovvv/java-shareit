package ru.practicum.shareit.booking.entity.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.user.entity.model.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

	private Long id;
	private LocalDateTime start;
	private LocalDateTime end;
	private Item item;
	private User booker;
	private BookingStatus status;
}
