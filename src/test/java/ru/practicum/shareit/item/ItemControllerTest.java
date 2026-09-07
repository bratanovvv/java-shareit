package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.utils.exception.impl.ForbiddenException;
import ru.practicum.shareit.item.entity.model.Item;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ItemService itemService;

	@Test
	void createShouldReturnCreatedItem() throws Exception {
		Item item = new Item(1L, "Drill", "Power drill", true, null, null);
		when(itemService.create(eq(1L), any(Item.class))).thenReturn(item);
		mockMvc.perform(post("/items")
						.header("X-Sharer-User-Id", 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"Drill\",\"description\":\"Power drill\",\"available\":true}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Drill"));
	}

	@Test
	void createShouldReturn400WhenAvailabilityMissing() throws Exception {
		mockMvc.perform(post("/items")
						.header("X-Sharer-User-Id", 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"Drill\",\"description\":\"Power drill\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void updateShouldReturn403ForNonOwner() throws Exception {
		when(itemService.update(eq(2L), eq(1L), any(Item.class)))
				.thenThrow(new ForbiddenException("Only the item owner can update the item"));
		mockMvc.perform(patch("/items/1")
						.header("X-Sharer-User-Id", 2)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"New drill\"}"))
				.andExpect(status().isForbidden());
	}

	@Test
	void updateShouldReturn400WhenNameBlank() throws Exception {
		mockMvc.perform(patch("/items/1")
						.header("X-Sharer-User-Id", 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\" \"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void searchShouldReturnFoundItems() throws Exception {
		when(itemService.search("drill")).thenReturn(List.of(new Item(1L, "Drill", "Power drill", true, null, null)));
		mockMvc.perform(get("/items/search").param("text", "drill"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].name").value("Drill"));
	}
}
