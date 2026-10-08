package ru.practicum.shareit.booking;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
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
import static ru.practicum.shareit.utils.TestDataFactory.bookItemRequestDto;

@WebMvcTest(BookingController.class)
class BookingControllerTest extends ControllerTestSupport {

	@MockBean
	private BookingClient bookingClient;

	@Test
	void bookItemShouldForwardToClient() throws Exception {
		BookItemRequestDto dto = bookItemRequestDto(1L,
				LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
		when(bookingClient.bookItem(anyLong(), any(BookItemRequestDto.class)))
				.thenReturn(new ResponseEntity<>(HttpStatus.OK));

		mockMvc.perform(post("/bookings")
						.header(RequestHeaders.USER_ID, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(dto)))
				.andExpect(status().isOk());
	}

	@Test
	void bookItemWithEndBeforeStartShouldReturnBadRequest() throws Exception {
		BookItemRequestDto dto = bookItemRequestDto(1L,
				LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1));

		mockMvc.perform(post("/bookings")
						.header(RequestHeaders.USER_ID, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(dto)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").exists());
	}

	@Test
	void bookItemWithPastEndShouldReturnBadRequest() throws Exception {
		BookItemRequestDto dto = bookItemRequestDto(1L,
				LocalDateTime.now().plusDays(1), LocalDateTime.now().minusDays(1));

		mockMvc.perform(post("/bookings")
						.header(RequestHeaders.USER_ID, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(dto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void approveShouldForwardToClient() throws Exception {
		when(bookingClient.approve(anyLong(), anyLong(), anyBoolean()))
				.thenReturn(new ResponseEntity<>(HttpStatus.OK));

		mockMvc.perform(patch("/bookings/1")
						.header(RequestHeaders.USER_ID, 1L)
						.param("approved", "true"))
				.andExpect(status().isOk());
	}

	@Test
	void getBookingsWithUnknownStateShouldReturnBadRequest() throws Exception {
		mockMvc.perform(get("/bookings")
						.header(RequestHeaders.USER_ID, 1L)
						.param("state", "UNKNOWN"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getBookingsShouldForwardToClient() throws Exception {
		when(bookingClient.getBookings(anyLong(), any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

		mockMvc.perform(get("/bookings").header(RequestHeaders.USER_ID, 1L))
				.andExpect(status().isOk());
	}
}
