package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.entity.dto.ItemRequestDto;
import ru.practicum.shareit.request.entity.model.ItemRequest;
import ru.practicum.shareit.utils.http.RequestHeaders;
import ru.practicum.shareit.utils.mapper.Mapper;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

	private final ItemRequestService itemRequestService;
	private final Mapper<ItemRequest, ItemRequestDto> mapper;

	public ItemRequestController(ItemRequestService itemRequestService,
								 Mapper<ItemRequest, ItemRequestDto> mapper) {
		this.itemRequestService = itemRequestService;
		this.mapper = mapper;
	}

	@PostMapping
	public ItemRequestDto create(@RequestHeader(RequestHeaders.USER_ID) long userId,
								 @RequestBody ItemRequestDto requestDto) {
		log.info("Creating request for user {}", userId);
		ItemRequest created = itemRequestService.create(userId, mapper.toEntity(requestDto));
		return mapper.toDto(created);
	}

	@GetMapping
	public List<ItemRequestDto> getByRequestor(@RequestHeader(RequestHeaders.USER_ID) long userId) {
		log.info("Getting requests of user {}", userId);
		return mapper.toDtoList(itemRequestService.getByRequestor(userId));
	}

	@GetMapping("/all")
	public List<ItemRequestDto> getByOthers(@RequestHeader(RequestHeaders.USER_ID) long userId) {
		log.info("Getting requests of other users for user {}", userId);
		return mapper.toDtoList(itemRequestService.getByOthers(userId));
	}

	@GetMapping("/{requestId}")
	public ItemRequestDto getById(@RequestHeader(RequestHeaders.USER_ID) long userId,
								  @PathVariable long requestId) {
		log.info("Getting request {} for user {}", requestId, userId);
		return mapper.toDto(itemRequestService.getById(userId, requestId));
	}
}
