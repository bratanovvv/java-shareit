package ru.practicum.shareit.user;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import ru.practicum.shareit.user.entity.UserMapper;
import ru.practicum.shareit.user.entity.dto.UserDto;
import ru.practicum.shareit.user.entity.model.User;
import ru.practicum.shareit.utils.ControllerTestSupport;
import ru.practicum.shareit.utils.exception.errors.impl.ConflictException;
import ru.practicum.shareit.utils.exception.errors.impl.NotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.TestDataFactory.user;

@WebMvcTest(UserController.class)
@Import(UserMapper.class)
class UserControllerTest extends ControllerTestSupport {

	@MockBean
	private UserService userService;

	@Test
	void createShouldReturnUser() throws Exception {
		when(userService.create(any(User.class))).thenReturn(user(1L, "John", "john@email.com"));

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(new UserDto(null, "John", "john@email.com"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("John"))
				.andExpect(jsonPath("$.email").value("john@email.com"));
	}

	@Test
	void createShouldReturnConflict() throws Exception {
		when(userService.create(any(User.class))).thenThrow(new ConflictException("Email is already in use"));

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(new UserDto(null, "John", "john@email.com"))))
				.andExpect(status().isConflict());
	}

	@Test
	void updateShouldReturnUser() throws Exception {
		when(userService.update(any(Long.class), any(User.class))).thenReturn(user(1L, "Jane", "jane@email.com"));

		mockMvc.perform(patch("/users/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(new UserDto(null, "Jane", "jane@email.com"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Jane"));
	}

	@Test
	void getByIdShouldReturnUser() throws Exception {
		when(userService.getById(1L)).thenReturn(user(1L, "John", "john@email.com"));

		mockMvc.perform(get("/users/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("John"));
	}

	@Test
	void getByIdShouldReturnNotFound() throws Exception {
		when(userService.getById(99L)).thenThrow(new NotFoundException("User with id 99 not found"));

		mockMvc.perform(get("/users/99"))
				.andExpect(status().isNotFound());
	}

	@Test
	void getAllShouldReturnUsers() throws Exception {
		when(userService.getAll()).thenReturn(List.of(
				user(1L, "John", "john@email.com"),
				user(2L, "Jane", "jane@email.com")));

		mockMvc.perform(get("/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].name").value("John"))
				.andExpect(jsonPath("$[1].name").value("Jane"));
	}

	@Test
	void deleteShouldCallService() throws Exception {
		doNothing().when(userService).delete(1L);

		mockMvc.perform(delete("/users/1"))
				.andExpect(status().isOk());

		verify(userService).delete(1L);
	}
}
