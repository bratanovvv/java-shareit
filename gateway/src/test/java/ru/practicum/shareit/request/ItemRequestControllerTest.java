package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.utils.ControllerTestSupport;
import ru.practicum.shareit.utils.http.RequestHeaders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.TestDataFactory.itemRequestDto;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest extends ControllerTestSupport {

	@MockBean
	private ItemRequestClient itemRequestClient;

	@Test
	void createShouldForwardToClient() throws Exception {
		when(itemRequestClient.createRequest(anyLong(), any(ItemRequestDto.class)))
				.thenReturn(new ResponseEntity<>(HttpStatus.OK));

		mockMvc.perform(post("/requests")
						.header(RequestHeaders.USER_ID, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(itemRequestDto("Need a drill"))))
				.andExpect(status().isOk());
	}

	@Test
	void createWithoutDescriptionShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/requests")
						.header(RequestHeaders.USER_ID, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(itemRequestDto(""))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").exists());
	}

	@Test
	void getRequestsShouldForwardToClient() throws Exception {
		when(itemRequestClient.getRequests(1L)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

		mockMvc.perform(get("/requests").header(RequestHeaders.USER_ID, 1L))
				.andExpect(status().isOk());
	}

	@Test
	void getAllRequestsShouldForwardToClient() throws Exception {
		when(itemRequestClient.getAllRequests(1L)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

		mockMvc.perform(get("/requests/all").header(RequestHeaders.USER_ID, 1L))
				.andExpect(status().isOk());
	}

	@Test
	void getRequestShouldForwardToClient() throws Exception {
		when(itemRequestClient.getRequest(1L, 1L)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

		mockMvc.perform(get("/requests/1").header(RequestHeaders.USER_ID, 1L))
				.andExpect(status().isOk());
	}
}
