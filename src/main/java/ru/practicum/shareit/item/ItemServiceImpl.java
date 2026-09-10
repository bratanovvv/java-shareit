package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.utils.exception.impl.ForbiddenException;
import ru.practicum.shareit.utils.exception.impl.NotFoundException;
import ru.practicum.shareit.item.entity.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.entity.model.User;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

	private final ItemStorage itemStorage;
	private final UserService userService;

	public ItemServiceImpl(ItemStorage itemStorage, UserService userService) {
		this.itemStorage = itemStorage;
		this.userService = userService;
	}

	@Override
	public Item create(long userId, Item item) {
		User owner = userService.getById(userId);
		item.setOwner(owner);
		return itemStorage.save(item);
	}

	@Override
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
		return itemStorage.save(existing);
	}

	@Override
	public Item getById(long itemId) {
		return itemStorage.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
	}

	@Override
	public List<Item> getByOwner(long userId) {
		return itemStorage.findByOwnerId(userId);
	}

	@Override
	public List<Item> search(String text) {
		if (text == null || text.isBlank()) {
			return List.of();
		}
		return itemStorage.search(text.trim());
	}
}
