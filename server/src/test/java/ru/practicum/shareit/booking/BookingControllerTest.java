package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import ru.practicum.shareit.booking.entity.BookingMapper;
import ru.practicum.shareit.booking.entity.dto.BookingDto;
import ru.practicum.shareit.booking.entity.model.Booking;
import ru.practicum.shareit.booking.entity.model.BookingStatus;
import ru.practicum.shareit.item.entity.CommentMapper;
import ru.practicum.shareit.item.entity.ItemMapper;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.user.entity.UserMapper;
import ru.practicum.shareit.user.entity.model.User;
import ru.practicum.shareit.utils.ControllerTestSupport;
import ru.practicum.shareit.utils.http.RequestHeaders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.TestDataFactory.booking;
import static ru.practicum.shareit.utils.TestDataFactory.item;
import static ru.practicum.shareit.utils.TestDataFactory.user;

@WebMvcTest(BookingController.class)
@Import({BookingMapper.class, ItemMapper.class, CommentMapper.class, UserMapper.class})
class BookingControllerTest extends ControllerTestSupport {

	@MockBean
	private BookingService bookingService;

	@Test
	void createShouldReturnBooking() throws Exception {
		LocalDateTime start = LocalDateTime.of(2026, 10, 9, 12, 0, 0);
		LocalDateTime end = LocalDateTime.of(2026, 10, 10, 12, 0, 0);
		Item item = item(1L, "Drill", "Power drill", true);
		User booker = user(2L, "John", "john@email.com");
		when(bookingService.create(anyLong(), any(Booking.class)))
				.thenReturn(booking(1L, start, end, item, booker, BookingStatus.WAITING));

		mockMvc.perform(post("/bookings")
						.header(RequestHeaders.USER_ID, 2L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(new BookingDto(null, start, end, 1L, null, null, null))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.status").value("WAITING"))
				.andExpect(jsonPath("$.booker.name").value("John"))
				.andExpect(jsonPath("$.item.name").value("Drill"));
	}

	@Test
	void approveShouldReturnBooking() throws Exception {
		LocalDateTime start = LocalDateTime.of(2026, 10, 9, 12, 0, 0);
		LocalDateTime end = LocalDateTime.of(2026, 10, 10, 12, 0, 0);
		Item item = item(1L, "Drill", "Power drill", true);
		User booker = user(2L, "John", "john@email.com");
		when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
				.thenReturn(booking(1L, start, end, item, booker, BookingStatus.APPROVED));

		mockMvc.perform(patch("/bookings/1")
						.header(RequestHeaders.USER_ID, 1L)
						.param("approved", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("APPROVED"));
	}

	@Test
	void getByIdShouldReturnBooking() throws Exception {
		LocalDateTime start = LocalDateTime.of(2026, 10, 9, 12, 0, 0);
		LocalDateTime end = LocalDateTime.of(2026, 10, 10, 12, 0, 0);
		Item item = item(1L, "Drill", "Power drill", true);
		User booker = user(2L, "John", "john@email.com");
		when(bookingService.getById(2L, 1L))
				.thenReturn(booking(1L, start, end, item, booker, BookingStatus.WAITING));

		mockMvc.perform(get("/bookings/1").header(RequestHeaders.USER_ID, 2L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.status").value("WAITING"));
	}

	@Test
	void getByBookerShouldReturnBookings() throws Exception {
		LocalDateTime start = LocalDateTime.of(2026, 10, 9, 12, 0, 0);
		LocalDateTime end = LocalDateTime.of(2026, 10, 10, 12, 0, 0);
		Item item = item(1L, "Drill", "Power drill", true);
		User booker = user(2L, "John", "john@email.com");
		when(bookingService.getByBooker(anyLong(), any())).thenReturn(List.of(
				booking(1L, start, end, item, booker, BookingStatus.WAITING)));

		mockMvc.perform(get("/bookings").header(RequestHeaders.USER_ID, 2L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(1));
	}

	@Test
	void getByOwnerShouldReturnBookings() throws Exception {
		LocalDateTime start = LocalDateTime.of(2026, 10, 9, 12, 0, 0);
		LocalDateTime end = LocalDateTime.of(2026, 10, 10, 12, 0, 0);
		Item item = item(1L, "Drill", "Power drill", true);
		User booker = user(2L, "John", "john@email.com");
		when(bookingService.getByOwner(anyLong(), any())).thenReturn(List.of(
				booking(1L, start, end, item, booker, BookingStatus.WAITING)));

		mockMvc.perform(get("/bookings/owner").header(RequestHeaders.USER_ID, 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(1));
	}
}
