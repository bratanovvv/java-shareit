package ru.practicum.shareit.booking.entity.dto;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.entity.model.BookingStatus;
import ru.practicum.shareit.user.entity.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

	@Autowired
	private JacksonTester<BookingDto> json;

	@Test
	void shouldSerializeDatesAsIsoAndStatusAsName() throws Exception {
		LocalDateTime start = LocalDateTime.of(2026, 10, 8, 12, 30, 0);
		LocalDateTime end = LocalDateTime.of(2026, 10, 9, 12, 30, 0);
		UserDto booker = new UserDto(1L, "booker", "booker@email.com");
		BookingDto dto = new BookingDto(1L, start, end, 2L, null, booker, BookingStatus.WAITING);

		String result = json.write(dto).getJson();

		assertThat(result)
				.contains("\"start\":\"2026-10-08T12:30:00\"")
				.contains("\"end\":\"2026-10-09T12:30:00\"")
				.contains("\"status\":\"WAITING\"")
				.contains("\"booker\":{\"id\":1,\"name\":\"booker\",\"email\":\"booker@email.com\"}");
	}

	@Test
	void shouldDeserializeIsoDatesAndStatus() throws Exception {
		String body = "{\"id\":1,\"start\":\"2026-10-08T12:30:00\",\"end\":\"2026-10-09T12:30:00\","
				+ "\"itemId\":2,\"status\":\"WAITING\"}";

		BookingDto dto = json.parse(body).getObject();

		assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 10, 8, 12, 30, 0));
		assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 10, 9, 12, 30, 0));
		assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
		assertThat(dto.getItemId()).isEqualTo(2L);
	}
}
