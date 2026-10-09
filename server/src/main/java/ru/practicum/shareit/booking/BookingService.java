package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.entity.dto.BookingState;
import ru.practicum.shareit.booking.entity.model.Booking;

import java.util.List;

public interface BookingService {

	Booking create(long userId, Booking booking);

	Booking approve(long userId, long bookingId, boolean approved);

	Booking getById(long userId, long bookingId);

	List<Booking> getByBooker(long userId, BookingState state);

	List<Booking> getByOwner(long userId, BookingState state);
}
