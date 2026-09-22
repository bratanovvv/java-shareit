package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.entity.dto.BookingState;
import ru.practicum.shareit.booking.entity.model.Booking;
import ru.practicum.shareit.booking.entity.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.entity.model.User;
import ru.practicum.shareit.utils.exception.errors.impl.ForbiddenException;
import ru.practicum.shareit.utils.exception.errors.impl.NotFoundException;
import ru.practicum.shareit.utils.exception.errors.impl.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

	private final BookingRepository bookingRepository;
	private final UserService userService;
	private final ItemService itemService;

	public BookingServiceImpl(BookingRepository bookingRepository, UserService userService, ItemService itemService) {
		this.bookingRepository = bookingRepository;
		this.userService = userService;
		this.itemService = itemService;
	}

	@Override
	@Transactional
	public Booking create(long userId, Booking booking) {
		User booker = userService.getById(userId);
		Item item = itemService.getById(booking.getItem().getId());
		if (!Boolean.TRUE.equals(item.getAvailable())) {
			throw new ValidationException("Item with id " + item.getId() + " is not available for booking");
		}
		if (item.getOwner().getId().equals(userId)) {
			throw new NotFoundException("Owner cannot book own item");
		}
		booking.setItem(item);
		booking.setBooker(booker);
		booking.setStatus(BookingStatus.WAITING);
		return bookingRepository.save(booking);
	}

	@Override
	@Transactional
	public Booking approve(long userId, long bookingId, boolean approved) {
		Booking booking = getBookingOrThrow(bookingId);
		if (!booking.getItem().getOwner().getId().equals(userId)) {
			throw new ForbiddenException("Booking with id " + bookingId + " not found for this owner");
		}
		if (booking.getStatus() != BookingStatus.WAITING) {
			throw new ValidationException("Booking status is already decided");
		}
		booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
		return bookingRepository.save(booking);
	}

	@Override
	@Transactional(readOnly = true)
	public Booking getById(long userId, long bookingId) {
		Booking booking = getBookingOrThrow(bookingId);
		boolean isBooker = booking.getBooker().getId().equals(userId);
		boolean isOwner = booking.getItem().getOwner().getId().equals(userId);
		if (!isBooker && !isOwner) {
			throw new NotFoundException("Booking with id " + bookingId + " not found for this user");
		}
		return booking;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Booking> getByBooker(long userId, BookingState state) {
		userService.getById(userId);
		LocalDateTime now = LocalDateTime.now();
		return switch (state) {
			case ALL -> bookingRepository.findAllByBooker(userId);
			case CURRENT -> bookingRepository.findCurrentByBooker(userId, now);
			case PAST -> bookingRepository.findPastByBooker(userId, now);
			case FUTURE -> bookingRepository.findFutureByBooker(userId, now);
			case WAITING -> bookingRepository.findByBookerAndStatus(userId, BookingStatus.WAITING);
			case REJECTED -> bookingRepository.findByBookerAndStatus(userId, BookingStatus.REJECTED);
		};
	}

	@Override
	@Transactional(readOnly = true)
	public List<Booking> getByOwner(long userId, BookingState state) {
		userService.getById(userId);
		LocalDateTime now = LocalDateTime.now();
		return switch (state) {
			case ALL -> bookingRepository.findAllByOwner(userId);
			case CURRENT -> bookingRepository.findCurrentByOwner(userId, now);
			case PAST -> bookingRepository.findPastByOwner(userId, now);
			case FUTURE -> bookingRepository.findFutureByOwner(userId, now);
			case WAITING -> bookingRepository.findByOwnerAndStatus(userId, BookingStatus.WAITING);
			case REJECTED -> bookingRepository.findByOwnerAndStatus(userId, BookingStatus.REJECTED);
		};
	}

	private Booking getBookingOrThrow(long bookingId) {
		return bookingRepository.findById(bookingId)
				.orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));
	}
}
