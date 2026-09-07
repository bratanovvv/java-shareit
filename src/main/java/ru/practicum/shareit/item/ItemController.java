package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.entity.ItemMapper;
import ru.practicum.shareit.item.entity.dto.ItemDto;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.utils.validation.Create;
import ru.practicum.shareit.utils.validation.Update;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	private final ItemService itemService;

	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}

	@PostMapping
	public ItemDto create(@RequestHeader(USER_ID_HEADER) long userId,
			@RequestBody @Validated(Create.class) ItemDto itemDto) {
		log.info("Creating item for owner {}", userId);
		Item created = itemService.create(userId, ItemMapper.toItem(itemDto));
		return ItemMapper.toItemDto(created);
	}

	@PatchMapping("/{itemId}")
	public ItemDto update(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long itemId,
			@RequestBody @Validated(Update.class) ItemDto itemDto) {
		log.info("Updating item {} by user {}", itemId, userId);
		Item updated = itemService.update(userId, itemId, ItemMapper.toItem(itemDto));
		return ItemMapper.toItemDto(updated);
	}

	@GetMapping("/{itemId}")
	public ItemDto getById(@PathVariable long itemId) {
		log.info("Getting item {}", itemId);
		return ItemMapper.toItemDto(itemService.getById(itemId));
	}

	@GetMapping
	public List<ItemDto> getByOwner(@RequestHeader(USER_ID_HEADER) long userId) {
		log.info("Getting items of owner {}", userId);
		return itemService.getByOwner(userId).stream()
				.map(ItemMapper::toItemDto)
				.toList();
	}

	@GetMapping("/search")
	public List<ItemDto> search(@RequestParam String text) {
		log.info("Searching items with text '{}'", text);
		return itemService.search(text).stream()
				.map(ItemMapper::toItemDto)
				.toList();
	}
}
