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
import ru.practicum.shareit.item.entity.dto.CommentDto;
import ru.practicum.shareit.item.entity.dto.ItemDto;
import ru.practicum.shareit.item.entity.model.Comment;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.utils.http.RequestHeaders;
import ru.practicum.shareit.utils.mapper.Mapper;
import ru.practicum.shareit.utils.validation.group.Create;
import ru.practicum.shareit.utils.validation.group.Update;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

	private final ItemService itemService;
	private final Mapper<Item, ItemDto> mapper;
	private final Mapper<Comment, CommentDto> commentMapper;

	public ItemController(ItemService itemService,
						  Mapper<Item, ItemDto> mapper,
						  Mapper<Comment, CommentDto> commentMapper) {
		this.itemService = itemService;
		this.mapper = mapper;
		this.commentMapper = commentMapper;
	}

	@PostMapping
	public ItemDto create(@RequestHeader(RequestHeaders.USER_ID) long userId,
						  @RequestBody @Validated(Create.class) ItemDto itemDto) {
		log.info("Creating item for owner {}", userId);
		Item created = itemService.create(userId, mapper.toEntity(itemDto));
		return mapper.toDto(created);
	}

	@PatchMapping("/{itemId}")
	public ItemDto update(@RequestHeader(RequestHeaders.USER_ID) long userId,
						  @PathVariable long itemId,
			@RequestBody @Validated(Update.class) ItemDto itemDto) {
		log.info("Updating item {} by user {}", itemId, userId);
		Item updated = itemService.update(userId, itemId, mapper.toEntity(itemDto));
		return mapper.toDto(updated);
	}

	@GetMapping("/{itemId}")
	public ItemDto getById(@RequestHeader(RequestHeaders.USER_ID) long userId,
						   @PathVariable long itemId) {
		log.info("Getting item {} for user {}", itemId, userId);
		return mapper.toDto(itemService.getById(userId, itemId));
	}

	@GetMapping
	public List<ItemDto> getByOwner(@RequestHeader(RequestHeaders.USER_ID) long userId) {
		log.info("Getting items of owner {}", userId);
		return mapper.toDtoList(itemService.getByOwner(userId));
	}

	@GetMapping("/search")
	public List<ItemDto> search(@RequestParam String text) {
		log.info("Searching items with text '{}'", text);
		return mapper.toDtoList(itemService.search(text));
	}

	@PostMapping("/{itemId}/comment")
	public CommentDto addComment(@RequestHeader(RequestHeaders.USER_ID) long userId,
								 @PathVariable long itemId,
								 @RequestBody @Validated(Create.class) CommentDto commentDto) {
		log.info("Adding comment to item {} by user {}", itemId, userId);
		Comment comment = itemService.addComment(userId, itemId, commentMapper.toEntity(commentDto));
		return commentMapper.toDto(comment);
	}
}
