package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.request.entity.ItemRequestMapper;
import ru.practicum.shareit.request.entity.dto.ItemRequestDto;
import ru.practicum.shareit.request.entity.model.ItemRequest;
import ru.practicum.shareit.utils.ControllerTestSupport;
import ru.practicum.shareit.utils.http.RequestHeaders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.TestDataFactory.item;
import static ru.practicum.shareit.utils.TestDataFactory.itemRequest;
import static ru.practicum.shareit.utils.TestDataFactory.user;

@WebMvcTest(ItemRequestController.class)
@Import(ItemRequestMapper.class)
class ItemRequestControllerTest extends ControllerTestSupport {

	@MockBean
	private ItemRequestService itemRequestService;

	@Test
	void createShouldReturnRequest() throws Exception {
		LocalDateTime created = LocalDateTime.of(2026, 10, 8, 12, 0, 0);
		when(itemRequestService.create(anyLong(), any(ItemRequest.class)))
				.thenReturn(itemRequest(1L, "Need a drill", created, null));

		mockMvc.perform(post("/requests")
						.header(RequestHeaders.USER_ID, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(new ItemRequestDto(null, "Need a drill", null, null))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.description").value("Need a drill"))
				.andExpect(jsonPath("$.items").isEmpty());
	}

	@Test
	void getByRequestorShouldReturnRequests() throws Exception {
		LocalDateTime created = LocalDateTime.of(2026, 10, 8, 12, 0, 0);
		when(itemRequestService.getByRequestor(1L)).thenReturn(List.of(
				itemRequest(1L, "Need a drill", created, null),
				itemRequest(2L, "Need a guitar", created, null)));

		mockMvc.perform(get("/requests").header(RequestHeaders.USER_ID, 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].description").value("Need a drill"))
				.andExpect(jsonPath("$[1].description").value("Need a guitar"));
	}

	@Test
	void getByOthersShouldReturnRequests() throws Exception {
		LocalDateTime created = LocalDateTime.of(2026, 10, 8, 12, 0, 0);
		when(itemRequestService.getByOthers(1L)).thenReturn(List.of(
				itemRequest(2L, "Need a guitar", created, null)));

		mockMvc.perform(get("/requests/all").header(RequestHeaders.USER_ID, 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].description").value("Need a guitar"));
	}

	@Test
	void getByIdShouldReturnRequestWithItems() throws Exception {
		LocalDateTime created = LocalDateTime.of(2026, 10, 8, 12, 0, 0);
		Item item = item(10L, "Drill", "Power drill", true, user(2L, "John", "john@email.com"));
		when(itemRequestService.getById(1L, 1L))
				.thenReturn(itemRequest(1L, "Need a drill", created, List.of(item)));

		mockMvc.perform(get("/requests/1").header(RequestHeaders.USER_ID, 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.items[0].id").value(10))
				.andExpect(jsonPath("$.items[0].name").value("Drill"))
				.andExpect(jsonPath("$.items[0].ownerId").value(2));
	}
}
