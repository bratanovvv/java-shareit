package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.utils.exception.impl.ForbiddenException;
import ru.practicum.shareit.utils.exception.impl.NotFoundException;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.utils.TestDataFactory.item;
import static ru.practicum.shareit.utils.TestDataFactory.user;

class ItemServiceImplTest {

	private ItemServiceImpl itemService;
	private UserService userService;
	private long ownerId;
	private long otherUserId;

	@BeforeEach
	void setUp() {
		userService = new UserServiceImpl(new InMemoryUserStorage());
		itemService = new ItemServiceImpl(new InMemoryItemStorage(), userService);
		ownerId = userService.create(user("owner", "owner@email.com")).getId();
		otherUserId = userService.create(user("other", "other@email.com")).getId();
	}

	@Test
	void createShouldAssignIdAndOwner() {
		Item created = itemService.create(ownerId, item("Drill", "Power drill", true));
		assertNotNull(created.getId());
		assertEquals(ownerId, created.getOwner().getId());
	}

	@Test
	void createShouldThrowWhenOwnerMissing() {
		assertThrows(NotFoundException.class, () -> itemService.create(99L, item("Drill", "Power drill", true)));
	}

	@Test
	void updateShouldPatchOnlyProvidedFields() {
		Item created = itemService.create(ownerId, item("Drill", "Power drill", true));
		Item updated = itemService.update(ownerId, created.getId(), item("Hammer drill", null, null));
		assertEquals("Hammer drill", updated.getName());
		assertEquals("Power drill", updated.getDescription());
		assertTrue(updated.getAvailable());
	}

	@Test
	void updateShouldThrowForNonOwner() {
		Item created = itemService.create(ownerId, item("Drill", "Power drill", true));
		assertThrows(ForbiddenException.class,
				() -> itemService.update(otherUserId, created.getId(), item("New drill", null, null)));
	}

	@Test
	void updateShouldThrowWhenItemMissing() {
		assertThrows(NotFoundException.class,
				() -> itemService.update(ownerId, 99L, item("New drill", null, null)));
	}

	@Test
	void getByIdShouldThrowWhenItemMissing() {
		assertThrows(NotFoundException.class, () -> itemService.getById(99L));
	}

	@Test
	void getByOwnerShouldReturnOnlyOwnedItems() {
		itemService.create(ownerId, item("Drill", "Power drill", true));
		itemService.create(otherUserId, item("Guitar", "Acoustic guitar", true));
		List<Item> owned = itemService.getByOwner(ownerId);
		assertEquals(1, owned.size());
		assertEquals("Drill", owned.get(0).getName());
	}

	@Test
	void searchShouldReturnOnlyAvailableMatchingItems() {
		itemService.create(ownerId, item("Аккумуляторная дрель", "Аккумуляторная дрель, 18 вольт", true));
		itemService.create(ownerId, item("Дрель", "Обычная дрель", false));
		itemService.create(ownerId, item("Гитара", "Акустическая гитара", true));
		List<Item> found = itemService.search("ДРЕЛЬ");
		assertEquals(1, found.size());
		assertEquals("Аккумуляторная дрель", found.get(0).getName());
	}

	@Test
	void searchShouldReturnEmptyListForBlankText() {
		itemService.create(ownerId, item("Drill", "Power drill", true));
		assertTrue(itemService.search("   ").isEmpty());
	}
}
