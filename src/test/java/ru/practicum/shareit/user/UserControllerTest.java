package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.utils.exception.impl.ConflictException;
import ru.practicum.shareit.utils.exception.impl.NotFoundException;
import ru.practicum.shareit.user.entity.model.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Test
	void createShouldReturnCreatedUser() throws Exception {
		when(userService.create(any(User.class))).thenReturn(new User(1L, "user", "user@email.com"));
		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"user\",\"email\":\"user@email.com\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("user"));
	}

	@Test
	void createShouldReturn400WhenNameMissing() throws Exception {
		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\":\"user@email.com\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createShouldReturn400WhenEmailInvalid() throws Exception {
		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"user\",\"email\":\"useremail.com\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createShouldReturn409WhenEmailTaken() throws Exception {
		when(userService.create(any(User.class))).thenThrow(new ConflictException("Email is already in use"));
		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"user\",\"email\":\"user@email.com\"}"))
				.andExpect(status().isConflict());
	}

	@Test
	void updateShouldAllowMissingFields() throws Exception {
		when(userService.update(eq(1L), any(User.class))).thenReturn(new User(1L, "user", "user@email.com"));
		mockMvc.perform(patch("/users/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isOk());
	}

	@Test
	void updateShouldReturn400WhenEmailInvalid() throws Exception {
		mockMvc.perform(patch("/users/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\":\"useremail.com\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getByIdShouldReturn404WhenUserMissing() throws Exception {
		when(userService.getById(99L)).thenThrow(new NotFoundException("User with id 99 not found"));
		mockMvc.perform(get("/users/99"))
				.andExpect(status().isNotFound());
	}
}
