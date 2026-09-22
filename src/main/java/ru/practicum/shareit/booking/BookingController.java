package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.entity.dto.BookingDto;
import ru.practicum.shareit.booking.entity.dto.BookingState;
import ru.practicum.shareit.booking.entity.model.Booking;
import ru.practicum.shareit.utils.mapper.Mapper;
import ru.practicum.shareit.utils.validation.group.Create;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
public class BookingController {

	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	private final BookingService bookingService;
	private final Mapper<Booking, BookingDto> mapper;

	public BookingController(BookingService bookingService,
							 Mapper<Booking, BookingDto> mapper) {
		this.bookingService = bookingService;
		this.mapper = mapper;
	}

	@PostMapping
	public BookingDto create(@RequestHeader(USER_ID_HEADER) long userId,
							 @RequestBody @Validated(Create.class) BookingDto bookingDto) {
		log.info("Creating booking for user {}", userId);
		Booking created = bookingService.create(userId, mapper.toEntity(bookingDto));
		return mapper.toDto(created);
	}

	@PatchMapping("/{bookingId}")
	public BookingDto approve(@RequestHeader(USER_ID_HEADER) long userId,
							  @PathVariable long bookingId,
							  @RequestParam boolean approved) {
		log.info("User {} sets approval={} for booking {}", userId, approved, bookingId);
		return mapper.toDto(bookingService.approve(userId, bookingId, approved));
	}

	@GetMapping("/{bookingId}")
	public BookingDto getById(@RequestHeader(USER_ID_HEADER) long userId,
							  @PathVariable long bookingId) {
		log.info("Getting booking {} for user {}", bookingId, userId);
		return mapper.toDto(bookingService.getById(userId, bookingId));
	}

	@GetMapping
	public List<BookingDto> getByBooker(@RequestHeader(USER_ID_HEADER) long userId,
										@RequestParam(defaultValue = "ALL") BookingState state) {
		log.info("Getting bookings of user {} with state {}", userId, state);
		return bookingService.getByBooker(userId, state).stream()
				.map(mapper::toDto)
				.toList();
	}

	@GetMapping("/owner")
	public List<BookingDto> getByOwner(@RequestHeader(USER_ID_HEADER) long userId,
									   @RequestParam(defaultValue = "ALL") BookingState state) {
		log.info("Getting bookings of items owned by user {} with state {}", userId, state);
		return bookingService.getByOwner(userId, state).stream()
				.map(mapper::toDto)
				.toList();
	}
}
