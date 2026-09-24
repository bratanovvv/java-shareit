package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.entity.dto.BookingState;
import ru.practicum.shareit.booking.entity.model.Booking;
import ru.practicum.shareit.booking.entity.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.utils.exception.errors.impl.ForbiddenException;
import ru.practicum.shareit.utils.exception.errors.impl.NotFoundException;
import ru.practicum.shareit.utils.exception.errors.impl.BusinessException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.utils.TestDataFactory.item;
import static ru.practicum.shareit.utils.TestDataFactory.user;

@DataJpaTest
class BookingServiceImplTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private CommentRepository commentRepository;

	private BookingServiceImpl bookingService;
	private long bookerId;
	private long ownerId;
	private long strangerId;
	private long itemId;

	@BeforeEach
	void setUp() {
		UserService userService = new UserServiceImpl(userRepository);
		ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, commentRepository);
		bookingService = new BookingServiceImpl(bookingRepository, userService, itemService);

		bookerId = userService.create(user("booker", "booker@email.com")).getId();
		ownerId = userService.create(user("owner", "owner@email.com")).getId();
		strangerId = userService.create(user("stranger", "stranger@email.com")).getId();
		itemId = itemService.create(ownerId, item("Drill", "Power drill", true)).getId();
	}

	@Test
	void createShouldAssignIdAndWaitingStatus() {
		Booking created = bookingService.create(bookerId, booking(itemId, LocalDateTime.now().plusDays(1),
				LocalDateTime.now().plusDays(2)));

		assertNotNull(created.getId());
		assertEquals(BookingStatus.WAITING, created.getStatus());
		assertEquals(bookerId, created.getBooker().getId());
		assertEquals(itemId, created.getItem().getId());
	}

	@Test
	void createShouldRejectUnavailableItem() {
		Item unavailable = itemRepository.save(itemWithOwner(ownerId, false));

		assertThrows(BusinessException.class, () -> bookingService.create(bookerId,
				booking(unavailable.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2))));
	}

	@Test
	void createShouldRejectOwnerBookingOwnItem() {
		assertThrows(NotFoundException.class, () -> bookingService.create(ownerId,
				booking(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2))));
	}

	@Test
	void createShouldThrowWhenItemMissing() {
		assertThrows(NotFoundException.class, () -> bookingService.create(bookerId,
				booking(99L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2))));
	}

	@Test
	void approveShouldChangeStatusForOwner() {
		Booking approvedBooking = bookingService.create(bookerId, booking(itemId, LocalDateTime.now().plusDays(1),
				LocalDateTime.now().plusDays(2)));
		assertEquals(BookingStatus.APPROVED,
				bookingService.approve(ownerId, approvedBooking.getId(), true).getStatus());

		Booking rejectedBooking = bookingService.create(bookerId, booking(itemId, LocalDateTime.now().plusDays(3),
				LocalDateTime.now().plusDays(4)));
		assertEquals(BookingStatus.REJECTED,
				bookingService.approve(ownerId, rejectedBooking.getId(), false).getStatus());
	}

	@Test
	void approveShouldRejectAlreadyDecidedBooking() {
		Booking created = bookingService.create(bookerId, booking(itemId, LocalDateTime.now().plusDays(1),
				LocalDateTime.now().plusDays(2)));
		bookingService.approve(ownerId, created.getId(), true);

		assertThrows(BusinessException.class, () -> bookingService.approve(ownerId, created.getId(), false));
	}

	@Test
	void approveShouldThrowForNonOwner() {
		Booking created = bookingService.create(bookerId, booking(itemId, LocalDateTime.now().plusDays(1),
				LocalDateTime.now().plusDays(2)));

		assertThrows(ForbiddenException.class, () -> bookingService.approve(bookerId, created.getId(), true));
	}

	@Test
	void getByIdShouldBeAvailableToBookerAndOwner() {
		Booking created = bookingService.create(bookerId, booking(itemId, LocalDateTime.now().plusDays(1),
				LocalDateTime.now().plusDays(2)));

		assertEquals(created.getId(), bookingService.getById(bookerId, created.getId()).getId());
		assertEquals(created.getId(), bookingService.getById(ownerId, created.getId()).getId());
	}

	@Test
	void getByIdShouldThrowForStranger() {
		Booking created = bookingService.create(bookerId, booking(itemId, LocalDateTime.now().plusDays(1),
				LocalDateTime.now().plusDays(2)));

		assertThrows(NotFoundException.class, () -> bookingService.getById(strangerId, created.getId()));
	}

	@Test
	void getByBookerShouldFilterByState() {
		LocalDateTime now = LocalDateTime.now();
		bookingService.create(bookerId, booking(itemId, now.minusDays(2), now.minusDays(1)));
		bookingService.create(bookerId, booking(itemId, now.minusHours(1), now.plusHours(1)));
		bookingService.create(bookerId, booking(itemId, now.plusDays(1), now.plusDays(2)));

		assertEquals(3, bookingService.getByBooker(bookerId, BookingState.ALL).size());
		assertEquals(1, bookingService.getByBooker(bookerId, BookingState.PAST).size());
		assertEquals(1, bookingService.getByBooker(bookerId, BookingState.CURRENT).size());
		assertEquals(1, bookingService.getByBooker(bookerId, BookingState.FUTURE).size());
		assertEquals(3, bookingService.getByBooker(bookerId, BookingState.WAITING).size());
		assertEquals(0, bookingService.getByBooker(bookerId, BookingState.REJECTED).size());
	}

	@Test
	void getByOwnerShouldReturnBookingsOfOwnedItems() {
		LocalDateTime now = LocalDateTime.now();
		Booking created = bookingService.create(bookerId, booking(itemId, now.plusDays(1), now.plusDays(2)));

		List<Booking> ownerBookings = bookingService.getByOwner(ownerId, BookingState.ALL);
		assertEquals(1, ownerBookings.size());
		assertEquals(created.getId(), ownerBookings.get(0).getId());
		assertEquals(0, bookingService.getByOwner(strangerId, BookingState.ALL).size());
	}

	private Booking booking(long itemId, LocalDateTime start, LocalDateTime end) {
		Item item = new Item();
		item.setId(itemId);
		Booking booking = new Booking();
		booking.setItem(item);
		booking.setStart(start);
		booking.setEnd(end);
		return booking;
	}

	private Item itemWithOwner(long ownerId, boolean available) {
		Item item = item("Unavailable", "Not for rent", available);
		item.setOwner(userRepository.findById(ownerId).orElseThrow());
		return item;
	}
}
