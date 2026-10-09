package ru.practicum.shareit.item;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {

	private static final String API_PREFIX = "/items";

	private static final String BY_ID = "/%d";

	private static final String COMMENT = "/%d/comment";

	private static final String SEARCH = "/search?text={text}";

	@Autowired
	public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
		super(
				builder
						.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
						.requestFactory(BaseClient::createRequestFactory)
						.build()
		);
	}

	public ResponseEntity<ItemDto> createItem(long userId, ItemDto itemDto) {
		return post("", userId, itemDto, ItemDto.class);
	}

	public ResponseEntity<ItemDto> updateItem(long userId, long itemId, ItemDto itemDto) {
		return patch(String.format(BY_ID, itemId), userId, itemDto, ItemDto.class);
	}

	public ResponseEntity<ItemDto> getItem(long userId, long itemId) {
		return get(String.format(BY_ID, itemId), userId, ItemDto.class);
	}

	public ResponseEntity<ItemDto[]> getItems(long userId) {
		return get("", userId, ItemDto[].class);
	}

	public ResponseEntity<ItemDto[]> search(String text) {
		return get(SEARCH, null, Map.of("text", text), ItemDto[].class);
	}

	public ResponseEntity<CommentDto> addComment(long userId, long itemId, CommentDto commentDto) {
		return post(String.format(COMMENT, itemId), userId, commentDto, CommentDto.class);
	}
}
