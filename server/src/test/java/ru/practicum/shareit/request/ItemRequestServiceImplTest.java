package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.entity.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.entity.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.utils.exception.errors.impl.NotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.utils.TestDataFactory.user;

@DataJpaTest
class ItemRequestServiceImplTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private ItemRequestRepository itemRequestRepository;

	private ItemRequestServiceImpl itemRequestService;
	private UserService userService;
	private long requestorId;
	private long otherUserId;

	@BeforeEach
	void setUp() {
		userService = new UserServiceImpl(userRepository);
		itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService, itemRepository);
		requestorId = userService.create(user("requestor", "requestor@email.com")).getId();
		otherUserId = userService.create(user("other", "other@email.com")).getId();
	}

	@Test
	void createShouldAssignIdRequestorAndCreated() {
		ItemRequest created = itemRequestService.create(requestorId, request("Need a drill"));

		assertNotNull(created.getId());
		assertEquals(requestorId, created.getRequestor().getId());
		assertNotNull(created.getCreated());
	}

	@Test
	void createShouldThrowWhenUserMissing() {
		assertThrows(NotFoundException.class, () -> itemRequestService.create(99L, request("Need a drill")));
	}

	@Test
	void getByRequestorShouldReturnOnlyOwnRequestsSortedDesc() {
		ItemRequest older = itemRequestService.create(requestorId, request("Older request"));
		ItemRequest newer = itemRequestService.create(requestorId, request("Newer request"));
		itemRequestService.create(otherUserId, request("Other request"));

		List<ItemRequest> own = itemRequestService.getByRequestor(requestorId);

		assertEquals(2, own.size());
		assertEquals(newer.getId(), own.get(0).getId());
		assertEquals(older.getId(), own.get(1).getId());
	}

	@Test
	void getByOthersShouldReturnOnlyOtherUsersRequestsSortedDesc() {
		ItemRequest older = itemRequestService.create(otherUserId, request("Older request"));
		ItemRequest newer = itemRequestService.create(otherUserId, request("Newer request"));
		itemRequestService.create(requestorId, request("Own request"));

		List<ItemRequest> others = itemRequestService.getByOthers(requestorId);

		assertEquals(2, others.size());
		assertEquals(newer.getId(), others.get(0).getId());
		assertEquals(older.getId(), others.get(1).getId());
	}

	@Test
	void getByOthersShouldReturnEmptyListWhenNoOtherRequests() {
		itemRequestService.create(requestorId, request("Own request"));

		assertTrue(itemRequestService.getByOthers(requestorId).isEmpty());
	}

	@Test
	void getByIdShouldReturnRequestWithItems() {
		ItemRequest created = itemRequestService.create(requestorId, request("Need a drill"));
		itemRepository.save(item("Drill", "Power drill", true, requestorId, created));

		ItemRequest loaded = itemRequestService.getById(otherUserId, created.getId());

		assertEquals(created.getId(), loaded.getId());
		assertEquals(1, loaded.getItems().size());
		assertEquals("Drill", loaded.getItems().get(0).getName());
	}

	@Test
	void getByIdShouldReturnRequestWithEmptyItemsWhenNoAnswers() {
		ItemRequest created = itemRequestService.create(requestorId, request("Need a drill"));

		ItemRequest loaded = itemRequestService.getById(otherUserId, created.getId());

		assertNotNull(loaded);
		assertTrue(loaded.getItems().isEmpty());
	}

	@Test
	void getByIdShouldThrowWhenRequestMissing() {
		assertThrows(NotFoundException.class, () -> itemRequestService.getById(requestorId, 99L));
	}

	@Test
	void getByRequestorShouldIncludeItemsForEachRequest() {
		ItemRequest first = itemRequestService.create(requestorId, request("Need a drill"));
		ItemRequest second = itemRequestService.create(requestorId, request("Need a guitar"));
		itemRepository.save(item("Drill", "Power drill", true, otherUserId, first));
		itemRepository.save(item("Guitar", "Acoustic guitar", true, otherUserId, second));

		List<ItemRequest> own = itemRequestService.getByRequestor(requestorId);

		assertEquals(1, own.get(0).getItems().size());
		assertEquals(1, own.get(1).getItems().size());
	}

	private ItemRequest request(String description) {
		return new ItemRequest(null, description, null, null, null);
	}

	private Item item(String name, String description, boolean available, long ownerId, ItemRequest request) {
		User owner = userRepository.findById(ownerId).orElseThrow();
		Item item = new Item();
		item.setName(name);
		item.setDescription(description);
		item.setAvailable(available);
		item.setOwner(owner);
		item.setRequest(request);
		return item;
	}
}
