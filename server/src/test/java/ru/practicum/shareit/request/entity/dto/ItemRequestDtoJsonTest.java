package ru.practicum.shareit.request.entity.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

	@Autowired
	private JacksonTester<ItemRequestDto> json;

	@Test
	void shouldSerializeCreatedAsIsoAndNestedItems() throws Exception {
		LocalDateTime created = LocalDateTime.of(2026, 10, 8, 12, 0, 0);
		RequestAnswerDto answer = new RequestAnswerDto(10L, "Drill", 3L);
		ItemRequestDto dto = new ItemRequestDto(1L, "Need a drill", created, List.of(answer));

		String result = json.write(dto).getJson();

		assertThat(result)
				.contains("\"created\":\"2026-10-08T12:00:00\"")
				.contains("\"items\":[{\"id\":10,\"name\":\"Drill\",\"ownerId\":3}]");
	}

	@Test
	void shouldSerializeEmptyItemsAsEmptyArray() throws Exception {
		ItemRequestDto dto = new ItemRequestDto(1L, "Need a drill", LocalDateTime.of(2026, 10, 8, 12, 0, 0), List.of());

		String result = json.write(dto).getJson();

		assertThat(result).contains("\"items\":[]");
	}
}
