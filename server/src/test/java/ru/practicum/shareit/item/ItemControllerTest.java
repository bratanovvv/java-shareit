package ru.practicum.shareit.item;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import ru.practicum.shareit.item.entity.CommentMapper;
import ru.practicum.shareit.item.entity.ItemMapper;
import ru.practicum.shareit.item.entity.dto.CommentDto;
import ru.practicum.shareit.item.entity.dto.ItemDto;
import ru.practicum.shareit.item.entity.model.Comment;
import ru.practicum.shareit.item.entity.model.Item;
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
import static ru.practicum.shareit.utils.TestDataFactory.comment;
import static ru.practicum.shareit.utils.TestDataFactory.item;
import static ru.practicum.shareit.utils.TestDataFactory.user;

@WebMvcTest(ItemController.class)
@Import({ItemMapper.class, CommentMapper.class})
class ItemControllerTest extends ControllerTestSupport {

	@MockBean
	private ItemService itemService;

	@Test
	void createShouldReturnItem() throws Exception {
		when(itemService.create(anyLong(), any(Item.class))).thenReturn(item(1L, "Drill", "Power drill", true));

		mockMvc.perform(post("/items")
						.header(RequestHeaders.USER_ID, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(new ItemDto(null, "Drill", "Power drill", true, null, null, null, null))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Drill"))
				.andExpect(jsonPath("$.available").value(true));
	}

	@Test
	void createWithoutHeaderShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/items")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(new ItemDto(null, "Drill", "Power drill", true, null, null, null, null))))
				.andExpect(status().isBadRequest());
	}

	@Test
	void updateShouldReturnItem() throws Exception {
		when(itemService.update(anyLong(), anyLong(), any(Item.class))).thenReturn(item(1L, "Hammer drill", "Power drill", true));

		mockMvc.perform(patch("/items/1")
						.header(RequestHeaders.USER_ID, 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(new ItemDto(null, "Hammer drill", null, null, null, null, null, null))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Hammer drill"));
	}

	@Test
	void getByIdShouldReturnItem() throws Exception {
		when(itemService.getById(1L, 1L)).thenReturn(item(1L, "Drill", "Power drill", true));

		mockMvc.perform(get("/items/1").header(RequestHeaders.USER_ID, 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Drill"));
	}

	@Test
	void getByOwnerShouldReturnItems() throws Exception {
		when(itemService.getByOwner(1L)).thenReturn(List.of(
				item(1L, "Drill", "Power drill", true),
				item(2L, "Guitar", "Acoustic guitar", true)));

		mockMvc.perform(get("/items").header(RequestHeaders.USER_ID, 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].name").value("Drill"))
				.andExpect(jsonPath("$[1].name").value("Guitar"));
	}

	@Test
	void searchShouldReturnItems() throws Exception {
		when(itemService.search("drill")).thenReturn(List.of(item(1L, "Drill", "Power drill", true)));

		mockMvc.perform(get("/items/search").param("text", "drill"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].name").value("Drill"));
	}

	@Test
	void addCommentShouldReturnComment() throws Exception {
		Comment saved = comment(1L, "Great drill", user(2L, "John", "john@email.com"), LocalDateTime.of(2026, 10, 8, 12, 0, 0));
		when(itemService.addComment(anyLong(), anyLong(), any(Comment.class))).thenReturn(saved);

		mockMvc.perform(post("/items/1/comment")
						.header(RequestHeaders.USER_ID, 2L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json(new CommentDto(null, "Great drill", null, null))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.text").value("Great drill"))
				.andExpect(jsonPath("$.authorName").value("John"));
	}

	@Test
	void searchShouldRequireTextParam() throws Exception {
		when(itemService.search(anyString())).thenReturn(List.of());

		mockMvc.perform(get("/items/search"))
				.andExpect(status().isBadRequest());
	}
}
