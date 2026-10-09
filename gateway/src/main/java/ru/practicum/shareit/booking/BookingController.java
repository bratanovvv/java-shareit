package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.utils.http.RequestHeaders;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<BookingDto> bookItem(@RequestHeader(RequestHeaders.USER_ID) long userId,
											   @RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<BookingDto> approve(@RequestHeader(RequestHeaders.USER_ID) long userId,
											  @PathVariable long bookingId,
											  @RequestParam boolean approved) {
		log.info("User {} sets approval={} for booking {}", userId, approved, bookingId);
		return bookingClient.approve(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<BookingDto> getBooking(@RequestHeader(RequestHeaders.USER_ID) long userId,
												 @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<BookingDto[]> getBookings(@RequestHeader(RequestHeaders.USER_ID) long userId,
													@RequestParam(name = "state", defaultValue = "all") String stateParam) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get bookings with state {}, userId={}", stateParam, userId);
		return bookingClient.getBookings(userId, state);
	}

	@GetMapping("/owner")
	public ResponseEntity<BookingDto[]> getBookingsByOwner(@RequestHeader(RequestHeaders.USER_ID) long userId,
														   @RequestParam(name = "state", defaultValue = "all") String stateParam) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get bookings of items owned by user {} with state {}", userId, stateParam);
		return bookingClient.getBookingsByOwner(userId, state);
	}
}
