package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.utils.http.RequestHeaders;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

	private final ItemRequestClient itemRequestClient;

	@PostMapping
	public ResponseEntity<Object> createRequest(@RequestHeader(RequestHeaders.USER_ID) long userId,
												@RequestBody @Valid ItemRequestDto requestDto) {
		log.info("Creating request {} for user {}", requestDto, userId);
		return itemRequestClient.createRequest(userId, requestDto);
	}

	@GetMapping
	public ResponseEntity<Object> getRequests(@RequestHeader(RequestHeaders.USER_ID) long userId) {
		log.info("Getting requests of user {}", userId);
		return itemRequestClient.getRequests(userId);
	}

	@GetMapping("/all")
	public ResponseEntity<Object> getAllRequests(@RequestHeader(RequestHeaders.USER_ID) long userId) {
		log.info("Getting requests of other users for user {}", userId);
		return itemRequestClient.getAllRequests(userId);
	}

	@GetMapping("/{requestId}")
	public ResponseEntity<Object> getRequest(@RequestHeader(RequestHeaders.USER_ID) long userId,
											 @PathVariable long requestId) {
		log.info("Getting request {} for user {}", requestId, userId);
		return itemRequestClient.getRequest(userId, requestId);
	}
}
