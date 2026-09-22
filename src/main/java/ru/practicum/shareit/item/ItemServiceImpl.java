package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.entity.model.User;
import ru.practicum.shareit.utils.exception.errors.impl.ForbiddenException;
import ru.practicum.shareit.utils.exception.errors.impl.NotFoundException;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

	private final ItemRepository itemRepository;
	private final UserService userService;

	public ItemServiceImpl(ItemRepository itemRepository, UserService userService) {
		this.itemRepository = itemRepository;
		this.userService = userService;
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
	public List<Item> getByOwner(long userId) {
		return itemRepository.findAllByOwnerId(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Item> search(String text) {
		if (text == null || text.isBlank()) {
			return List.of();
		}
		return itemRepository.search(text.trim());
	}
}
