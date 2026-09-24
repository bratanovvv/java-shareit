package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.entity.model.Booking;
import ru.practicum.shareit.booking.entity.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.entity.model.Comment;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.entity.model.User;
import ru.practicum.shareit.utils.exception.errors.impl.BusinessException;
import ru.practicum.shareit.utils.exception.errors.impl.ForbiddenException;
import ru.practicum.shareit.utils.exception.errors.impl.NotFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

	private final ItemRepository itemRepository;
	private final UserService userService;
	private final BookingRepository bookingRepository;
	private final CommentRepository commentRepository;

	public ItemServiceImpl(ItemRepository itemRepository,
						   UserService userService,
						   BookingRepository bookingRepository,
						   CommentRepository commentRepository) {
		this.itemRepository = itemRepository;
		this.userService = userService;
		this.bookingRepository = bookingRepository;
		this.commentRepository = commentRepository;
	}

	@Override
	@Transactional
	public Item create(long userId, Item item) {
		User owner = userService.getById(userId);
		item.setOwner(owner);
		return itemRepository.save(item);
	}

	@Override
	@Transactional
	public Item update(long userId, long itemId, Item patch) {
		Item existing = getById(itemId);
		if (!existing.getOwner().getId().equals(userId)) {
			throw new ForbiddenException("Only the item owner can update the item");
		}
		if (patch.getName() != null) {
			existing.setName(patch.getName());
		}
		if (patch.getDescription() != null) {
			existing.setDescription(patch.getDescription());
		}
		if (patch.getAvailable() != null) {
			existing.setAvailable(patch.getAvailable());
		}
		return itemRepository.save(existing);
	}

	@Override
	@Transactional(readOnly = true)
	public Item getById(long itemId) {
		return itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
	}

	@Override
	@Transactional(readOnly = true)
	public Item getById(long userId, long itemId) {
		Item item = getById(itemId);
		if (item.getOwner().getId().equals(userId)) {
			addBookings(List.of(item));
		} else {
			item.setLastBooking(null);
			item.setNextBooking(null);
		}
		addComments(List.of(item));
		return item;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Item> getByOwner(long userId) {
		List<Item> items = itemRepository.findAllByOwnerId(userId);
		if (items.isEmpty()) {
			return items;
		}
		addBookings(items);
		addComments(items);
		return items;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Item> search(String text) {
		if (text == null || text.isBlank()) {
			return List.of();
		}
		return itemRepository.search(text.trim());
	}

	@Override
	@Transactional
	public Comment addComment(long userId, long itemId, Comment comment) {
		User author = userService.getById(userId);
		Item item = getById(itemId);
		LocalDateTime now = LocalDateTime.now();
		boolean hasCompletedBooking = !bookingRepository
				.findCompletedBookings(userId, itemId, BookingStatus.APPROVED, now)
				.isEmpty();
		if (!hasCompletedBooking) {
			throw new BusinessException("User " + userId + " has not rented item " + itemId);
		}
		comment.setItem(item);
		comment.setAuthor(author);
		comment.setCreated(now);
		return commentRepository.save(comment);
	}

	private void addBookings(List<Item> items) {
		LocalDateTime now = LocalDateTime.now();
		List<Long> itemIds = items.stream().map(Item::getId).toList();
		Map<Long, Booking> lastBookings = firstByItem(
				bookingRepository.findLastBookings(itemIds, BookingStatus.APPROVED, now));
		Map<Long, Booking> nextBookings = firstByItem(
				bookingRepository.findNextBookings(itemIds, BookingStatus.APPROVED, now));
		items.forEach(item -> {
			item.setLastBooking(lastBookings.get(item.getId()));
			item.setNextBooking(nextBookings.get(item.getId()));
		});
	}

	private void addComments(List<Item> items) {
		List<Long> itemIds = items.stream().map(Item::getId).toList();
		Map<Long, List<Comment>> comments = commentRepository.findByItemIdIn(itemIds).stream()
				.collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
		items.forEach(item -> item.setComments(comments.getOrDefault(item.getId(), List.of())));
	}

	private Map<Long, Booking> firstByItem(List<Booking> bookings) {
		Map<Long, Booking> result = new HashMap<>();
		for (Booking booking : bookings) {
			result.putIfAbsent(booking.getItem().getId(), booking);
		}
		return result;
	}
}
