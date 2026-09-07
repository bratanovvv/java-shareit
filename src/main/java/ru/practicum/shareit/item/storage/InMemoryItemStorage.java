package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.entity.model.Item;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryItemStorage implements ItemStorage {

	private final Map<Long, Item> items = new LinkedHashMap<>();
	private final AtomicLong idGenerator = new AtomicLong();

	@Override
	public Item save(Item item) {
		if (item.getId() == null) {
			item.setId(idGenerator.incrementAndGet());
		}
		items.put(item.getId(), item);
		return item;
	}

	@Override
	public Optional<Item> findById(Long id) {
		return Optional.ofNullable(items.get(id));
	}

	@Override
	public List<Item> findByOwnerId(Long ownerId) {
		return items.values().stream()
				.filter(item -> item.getOwner().getId().equals(ownerId))
				.toList();
	}

	@Override
	public List<Item> search(String text) {
		String query = text.toLowerCase(Locale.ROOT);
		return items.values().stream()
				.filter(item -> Boolean.TRUE.equals(item.getAvailable()))
				.filter(item -> matchesQuery(item, query))
				.toList();
	}

	private boolean matchesQuery(Item item, String query) {
		return item.getName().toLowerCase(Locale.ROOT).contains(query)
				|| item.getDescription().toLowerCase(Locale.ROOT).contains(query);
	}
}
