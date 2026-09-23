package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.entity.model.Booking;
import ru.practicum.shareit.booking.entity.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.entity.model.Comment;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.utils.exception.errors.impl.BusinessException;
import ru.practicum.shareit.utils.exception.errors.impl.ForbiddenException;
import ru.practicum.shareit.utils.exception.errors.impl.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.utils.TestDataFactory.item;
import static ru.practicum.shareit.utils.TestDataFactory.user;

@DataJpaTest
class ItemServiceImplTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private CommentRepository commentRepository;

	private ItemServiceImpl itemService;
	private UserService userService;
	private long ownerId;
	private long otherUserId;

	@BeforeEach
	void setUp() {
		userService = new UserServiceImpl(userRepository);
		itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, commentRepository);
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

	@Test
	void getByOwnerShouldIncludeLastAndNextApprovedBookings() {
		Item created = itemService.create(ownerId, item("Drill", "Power drill", true));
		LocalDateTime now = LocalDateTime.now();
		bookingRepository.save(booking(created.getId(), otherUserId, now.minusDays(2), now.minusDays(1),
				BookingStatus.APPROVED));
		bookingRepository.save(booking(created.getId(), otherUserId, now.plusDays(1), now.plusDays(2),
				BookingStatus.APPROVED));

		Item loaded = itemService.getByOwner(ownerId).get(0);
		assertNotNull(loaded.getLastBooking());
		assertNotNull(loaded.getNextBooking());
		assertEquals(created.getId(), loaded.getLastBooking().getItem().getId());
	}

	@Test
	void getByOwnerShouldIgnoreNotApprovedBookings() {
		Item created = itemService.create(ownerId, item("Drill", "Power drill", true));
		LocalDateTime now = LocalDateTime.now();
		bookingRepository.save(booking(created.getId(), otherUserId, now.minusDays(2), now.minusDays(1),
				BookingStatus.REJECTED));
		bookingRepository.save(booking(created.getId(), otherUserId, now.plusDays(1), now.plusDays(2),
				BookingStatus.WAITING));

		Item loaded = itemService.getByOwner(ownerId).get(0);
		assertNull(loaded.getLastBooking());
		assertNull(loaded.getNextBooking());
	}

	@Test
	void getByIdShouldExposeBookingsToOwner() {
		Item created = itemService.create(ownerId, item("Drill", "Power drill", true));
		LocalDateTime now = LocalDateTime.now();
		bookingRepository.save(booking(created.getId(), otherUserId, now.minusDays(2), now.minusDays(1),
				BookingStatus.APPROVED));

		Item forOwner = itemService.getById(ownerId, created.getId());
		assertNotNull(forOwner.getLastBooking());
	}

	@Test
	void getByIdShouldHideBookingsFromNonOwner() {
		Item created = itemService.create(ownerId, item("Drill", "Power drill", true));
		LocalDateTime now = LocalDateTime.now();
		bookingRepository.save(booking(created.getId(), otherUserId, now.minusDays(2), now.minusDays(1),
				BookingStatus.APPROVED));

		Item forOther = itemService.getById(otherUserId, created.getId());
		assertNull(forOther.getLastBooking());
		assertNull(forOther.getNextBooking());
	}

	@Test
	void addCommentShouldSaveWhenUserHasCompletedBooking() {
		Item created = itemService.create(ownerId, item("Drill", "Power drill", true));
		LocalDateTime now = LocalDateTime.now();
		bookingRepository.save(booking(created.getId(), otherUserId, now.minusDays(2), now.minusDays(1),
				BookingStatus.APPROVED));

		Comment saved = itemService.addComment(otherUserId, created.getId(), comment("Great drill"));

		assertNotNull(saved.getId());
		assertEquals(otherUserId, saved.getAuthor().getId());
		assertNotNull(saved.getCreated());
	}

	@Test
	void addCommentShouldThrowWithoutCompletedBooking() {
		Item created = itemService.create(ownerId, item("Drill", "Power drill", true));
		LocalDateTime now = LocalDateTime.now();
		bookingRepository.save(booking(created.getId(), otherUserId, now.plusDays(1), now.plusDays(2),
				BookingStatus.APPROVED));

		assertThrows(BusinessException.class,
				() -> itemService.addComment(otherUserId, created.getId(), comment("Not yet")));
	}

	@Test
	void getByIdShouldExposeCommentsToNonOwner() {
		Item created = itemService.create(ownerId, item("Drill", "Power drill", true));
		LocalDateTime now = LocalDateTime.now();
		bookingRepository.save(booking(created.getId(), otherUserId, now.minusDays(2), now.minusDays(1),
				BookingStatus.APPROVED));
		itemService.addComment(otherUserId, created.getId(), comment("Great drill"));

		Item forOther = itemService.getById(otherUserId, created.getId());
		assertEquals(1, forOther.getComments().size());
		assertEquals("Great drill", forOther.getComments().get(0).getText());
	}

	private Comment comment(String text) {
		Comment comment = new Comment();
		comment.setText(text);
		return comment;
	}

	private Booking booking(long itemId, long bookerId, LocalDateTime start, LocalDateTime end, BookingStatus status) {
		Booking booking = new Booking();
		booking.setItem(itemRepository.findById(itemId).orElseThrow());
		booking.setBooker(userRepository.findById(bookerId).orElseThrow());
		booking.setStart(start);
		booking.setEnd(end);
		booking.setStatus(status);
		return booking;
	}
}
