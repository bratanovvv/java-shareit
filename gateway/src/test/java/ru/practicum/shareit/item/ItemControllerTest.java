package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.ControllerTestSupport;
import ru.practicum.shareit.utils.http.RequestHeaders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.TestDataFactory.commentDto;
import static ru.practicum.shareit.utils.TestDataFactory.itemDto;

@WebMvcTest(ItemController.class)
class ItemControllerTest extends ControllerTestSupport {

	@MockBean
	private ItemClient itemClient;

	@Test
	void createShouldForwardToClient() throws Exception {
		when(itemClient.createItem(anyLong(), any(ItemDto.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

		mockMvc.perform(post("/items")
						.header(RequestHeaders.USER_ID, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(itemDto(null, "Drill", "Power drill", true, null))))
				.andExpect(status().isOk());
	}

	@Test
	void createWithoutNameShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/items")
						.header(RequestHeaders.USER_ID, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(itemDto(null, null, "Power drill", true, null))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").exists());
	}

	@Test
	void createWithoutAvailableShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/items")
						.header(RequestHeaders.USER_ID, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(itemDto(null, "Drill", "Power drill", null, null))))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createWithoutHeaderShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/items")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(itemDto(null, "Drill", "Power drill", true, null))))
				.andExpect(status().isBadRequest());
	}

	@Test
	void searchShouldForwardToClient() throws Exception {
		when(itemClient.search(anyString())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

		mockMvc.perform(get("/items/search").param("text", "drill"))
				.andExpect(status().isOk());
	}

	@Test
	void updateShouldForwardToClient() throws Exception {
		when(itemClient.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
				.thenReturn(new ResponseEntity<>(HttpStatus.OK));

		mockMvc.perform(patch("/items/1")
						.header(RequestHeaders.USER_ID, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(itemDto(null, "Drill", "Power drill", true, null))))
				.andExpect(status().isOk());
	}

	@Test
	void getItemShouldForwardToClient() throws Exception {
		when(itemClient.getItem(1L, 1L)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

		mockMvc.perform(get("/items/1").header(RequestHeaders.USER_ID, 1L))
				.andExpect(status().isOk());
	}

	@Test
	void getItemsShouldForwardToClient() throws Exception {
		when(itemClient.getItems(1L)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

		mockMvc.perform(get("/items").header(RequestHeaders.USER_ID, 1L))
				.andExpect(status().isOk());
	}

	@Test
	void addCommentWithoutTextShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/items/1/comment")
						.header(RequestHeaders.USER_ID, 2L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(commentDto(null))))
				.andExpect(status().isBadRequest());
	}
}
