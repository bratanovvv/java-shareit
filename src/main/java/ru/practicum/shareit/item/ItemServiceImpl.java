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
import ru.practicum.shareit.utils.exception.errors.impl.ForbiddenException;
import ru.practicum.shareit.utils.exception.errors.impl.NotFoundException;
import ru.practicum.shareit.utils.exception.errors.impl.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

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
			addBookings(item);
		} else {
			item.setLastBooking(null);
			item.setNextBooking(null);
		}
		addComments(item);
		return item;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Item> getByOwner(long userId) {
		List<Item> items = itemRepository.findAllByOwnerId(userId);
		items.forEach(item -> {
			addBookings(item);
			addComments(item);
		});
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
			throw new ValidationException("User " + userId + " has not rented item " + itemId);
		}
		comment.setItem(item);
		comment.setAuthor(author);
		comment.setCreated(now);
		return commentRepository.save(comment);
	}

	private void addBookings(Item item) {
		LocalDateTime now = LocalDateTime.now();
		Booking lastBooking = bookingRepository.findLastBookings(item.getId(), BookingStatus.APPROVED, now)
				.stream()
				.findFirst()
				.orElse(null);
		Booking nextBooking = bookingRepository.findNextBookings(item.getId(), BookingStatus.APPROVED, now)
				.stream()
				.findFirst()
				.orElse(null);
		item.setLastBooking(lastBooking);
		item.setNextBooking(nextBooking);
	}

	private void addComments(Item item) {
		item.setComments(commentRepository.findByItemId(item.getId()));
	}
}
