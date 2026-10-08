package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.ControllerTestSupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.TestDataFactory.userDto;

@WebMvcTest(UserController.class)
class UserControllerTest extends ControllerTestSupport {

	@MockBean
	private UserClient userClient;

	@Test
	void createShouldForwardToClient() throws Exception {
		when(userClient.createUser(any(UserDto.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(userDto(null, "John", "john@email.com"))))
				.andExpect(status().isOk());
	}

	@Test
	void createWithoutEmailShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(userDto(null, "John", null))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").exists());
	}

	@Test
	void createWithInvalidEmailShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(userDto(null, "John", "not-an-email"))))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createWithMalformedJsonShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{invalid"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void updateShouldForwardToClient() throws Exception {
		when(userClient.updateUser(anyLong(), any(UserDto.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

		mockMvc.perform(patch("/users/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(userDto(null, "Jane", "jane@email.com"))))
				.andExpect(status().isOk());
	}

	@Test
	void getShouldForwardToClient() throws Exception {
		when(userClient.getUser(1L)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

		mockMvc.perform(get("/users/1"))
				.andExpect(status().isOk());
	}

	@Test
	void getAllShouldForwardToClient() throws Exception {
		when(userClient.getUsers()).thenReturn(new ResponseEntity<>(HttpStatus.OK));

		mockMvc.perform(get("/users"))
				.andExpect(status().isOk());
	}

	@Test
	void deleteShouldForwardToClient() throws Exception {
		when(userClient.deleteUser(1L)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

		mockMvc.perform(delete("/users/1"))
				.andExpect(status().isOk());
	}
}
