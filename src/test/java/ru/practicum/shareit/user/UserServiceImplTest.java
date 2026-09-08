package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.utils.exception.impl.ConflictException;
import ru.practicum.shareit.utils.exception.impl.NotFoundException;
import ru.practicum.shareit.user.entity.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.utils.TestDataFactory.user;

class UserServiceImplTest {

	private UserServiceImpl userService;

	@BeforeEach
	void setUp() {
		userService = new UserServiceImpl(new InMemoryUserStorage());
	}

	@Test
	void createShouldAssignId() {
		User created = userService.create(user("user", "user@email.com"));
		assertNotNull(created.getId());
		assertEquals("user", created.getName());
	}

	@Test
	void createShouldRejectDuplicateEmail() {
		userService.create(user("user", "user@email.com"));
		assertThrows(ConflictException.class, () -> userService.create(user("other", "user@email.com")));
	}

	@Test
	void updateShouldPatchOnlyProvidedFields() {
		User created = userService.create(user("user", "user@email.com"));
		User updated = userService.update(created.getId(), user(null, "new@email.com"));
		assertEquals("user", updated.getName());
		assertEquals("new@email.com", updated.getEmail());
	}

	@Test
	void updateShouldRejectEmailTakenByAnotherUser() {
		userService.create(user("one", "one@email.com"));
		User two = userService.create(user("two", "two@email.com"));
		assertThrows(ConflictException.class,
				() -> userService.update(two.getId(), user(null, "one@email.com")));
	}

	@Test
	void updateShouldThrowWhenUserMissing() {
		assertThrows(NotFoundException.class, () -> userService.update(99L, user(null, "x@email.com")));
	}

	@Test
	void getByIdShouldThrowWhenUserMissing() {
		assertThrows(NotFoundException.class, () -> userService.getById(99L));
	}

	@Test
	void getAllShouldReturnAllUsers() {
		userService.create(user("one", "one@email.com"));
		userService.create(user("two", "two@email.com"));
		assertEquals(2, userService.getAll().size());
	}

	@Test
	void deleteShouldRemoveUser() {
		User created = userService.create(user("user", "user@email.com"));
		userService.delete(created.getId());
		assertThrows(NotFoundException.class, () -> userService.getById(created.getId()));
	}
}
