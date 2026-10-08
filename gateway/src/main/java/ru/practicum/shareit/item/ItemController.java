package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.http.RequestHeaders;
import ru.practicum.shareit.utils.validation.group.Create;
import ru.practicum.shareit.utils.validation.group.Update;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

	private final ItemClient itemClient;

	@PostMapping
	public ResponseEntity<Object> createItem(@RequestHeader(RequestHeaders.USER_ID) long userId,
											 @RequestBody @Validated(Create.class) ItemDto itemDto) {
		log.info("Creating item {} for owner {}", itemDto, userId);
		return itemClient.createItem(userId, itemDto);
	}

	@PatchMapping("/{itemId}")
	public ResponseEntity<Object> updateItem(@RequestHeader(RequestHeaders.USER_ID) long userId,
											 @PathVariable long itemId,
											 @RequestBody @Validated(Update.class) ItemDto itemDto) {
		log.info("Updating item {} by user {}", itemId, userId);
		return itemClient.updateItem(userId, itemId, itemDto);
	}

	@GetMapping("/{itemId}")
	public ResponseEntity<Object> getItem(@RequestHeader(RequestHeaders.USER_ID) long userId,
										  @PathVariable long itemId) {
		log.info("Getting item {} for user {}", itemId, userId);
		return itemClient.getItem(userId, itemId);
	}

	@GetMapping
	public ResponseEntity<Object> getItems(@RequestHeader(RequestHeaders.USER_ID) long userId) {
		log.info("Getting items of owner {}", userId);
		return itemClient.getItems(userId);
	}

	@GetMapping("/search")
	public ResponseEntity<Object> search(@RequestParam String text) {
		log.info("Searching items with text '{}'", text);
		return itemClient.search(text);
	}

	@PostMapping("/{itemId}/comment")
	public ResponseEntity<Object> addComment(@RequestHeader(RequestHeaders.USER_ID) long userId,
											 @PathVariable long itemId,
											 @RequestBody @Valid CommentDto commentDto) {
		log.info("Adding comment to item {} by user {}", itemId, userId);
		return itemClient.addComment(userId, itemId, commentDto);
	}
}
