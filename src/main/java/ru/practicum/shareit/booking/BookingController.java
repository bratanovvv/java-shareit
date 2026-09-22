package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.entity.dto.BookingDto;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {


    @PostMapping
    public BookingDto createBookingRequest() {
        throw new UnsupportedOperationException("Booking creation is not implemented yet");
    }
}
