package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.entity.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.entity.model.User;
import ru.practicum.shareit.utils.exception.errors.impl.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

	private final ItemRequestRepository itemRequestRepository;
	private final UserService userService;
	private final ItemRepository itemRepository;

	public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
								  UserService userService,
								  ItemRepository itemRepository) {
		this.itemRequestRepository = itemRequestRepository;
		this.userService = userService;
		this.itemRepository = itemRepository;
	}

	@Override
	@Transactional
	public ItemRequest create(long userId, ItemRequest request) {
		User requestor = userService.getById(userId);
		request.setRequestor(requestor);
		request.setCreated(LocalDateTime.now());
		return itemRequestRepository.save(request);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemRequest> getByRequestor(long userId) {
		userService.checkExists(userId);
		List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(userId);
		addItems(requests);
		return requests;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemRequest> getByOthers(long userId) {
		userService.checkExists(userId);
		List<ItemRequest> requests = itemRequestRepository.findAllByOtherRequestors(userId);
		addItems(requests);
		return requests;
	}

	@Override
	@Transactional(readOnly = true)
	public ItemRequest getById(long userId, long requestId) {
		userService.checkExists(userId);
		ItemRequest request = itemRequestRepository.findById(requestId)
				.orElseThrow(() -> new NotFoundException("Request with id " + requestId + " not found"));
		addItems(List.of(request));
		return request;
	}

	private void addItems(List<ItemRequest> requests) {
		if (requests.isEmpty()) {
			return;
		}
		List<Long> requestIds = requests.stream().map(ItemRequest::getId).toList();
		Map<Long, List<Item>> itemsByRequest = itemRepository.findAllByRequestIdIn(requestIds).stream()
				.collect(Collectors.groupingBy(item -> item.getRequest().getId()));
		requests.forEach(request -> request.setItems(itemsByRequest.getOrDefault(request.getId(), List.of())));
	}
}
