package ru.practicum.shareit.booking;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.BaseClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

@Service
public class BookingClient extends BaseClient {

	private static final String API_PREFIX = "/bookings";

	private static final String BY_ID = "/%d";

	private static final String APPROVE = "/%d?approved={approved}";

	private static final String BY_STATE = "?state={state}";

	private static final String OWNER_BY_STATE = "/owner?state={state}";

	@Autowired
	public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
		super(
				builder
						.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
						.requestFactory(BaseClient::createRequestFactory)
						.build()
		);
	}

	public ResponseEntity<BookingDto[]> getBookings(long userId, BookingState state) {
		return get(BY_STATE, userId, Map.of("state", state.name()), BookingDto[].class);
	}

	public ResponseEntity<BookingDto[]> getBookingsByOwner(long userId, BookingState state) {
		return get(OWNER_BY_STATE, userId, Map.of("state", state.name()), BookingDto[].class);
	}

	public ResponseEntity<BookingDto> bookItem(long userId, BookItemRequestDto requestDto) {
		return post("", userId, requestDto, BookingDto.class);
	}

	public ResponseEntity<BookingDto> approve(long userId, long bookingId, boolean approved) {
		return patch(String.format(APPROVE, bookingId), userId, Map.of("approved", approved), null, BookingDto.class);
	}

	public ResponseEntity<BookingDto> getBooking(long userId, Long bookingId) {
		return get(String.format(BY_ID, bookingId), userId, BookingDto.class);
	}
}
