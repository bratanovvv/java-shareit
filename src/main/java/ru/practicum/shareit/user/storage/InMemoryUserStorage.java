package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.entity.model.User;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserStorage implements UserStorage {

	private final Map<Long, User> users = new LinkedHashMap<>();
	private final AtomicLong idGenerator = new AtomicLong();

	@Override
	public User save(User user) {
		if (user.getId() == null) {
			user.setId(idGenerator.incrementAndGet());
		}
		users.put(user.getId(), user);
		return user;
	}

	@Override
	public Optional<User> findById(Long id) {
		return Optional.ofNullable(users.get(id));
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return users.values().stream()
				.filter(user -> Objects.equals(user.getEmail(), email))
				.findFirst();
	}

	@Override
	public List<User> findAll() {
		return List.copyOf(users.values());
	}

	@Override
	public void deleteById(Long id) {
		users.remove(id);
	}
}
