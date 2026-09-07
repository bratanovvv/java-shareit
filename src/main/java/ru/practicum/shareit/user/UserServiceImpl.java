package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.utils.exception.impl.ConflictException;
import ru.practicum.shareit.utils.exception.impl.NotFoundException;
import ru.practicum.shareit.user.entity.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	private final UserStorage userStorage;

	public UserServiceImpl(UserStorage userStorage) {
		this.userStorage = userStorage;
	}

	@Override
	public User create(User user) {
		checkEmailNotTaken(user.getEmail(), null);
		return userStorage.save(user);
	}

	@Override
	public User update(long userId, User user) {
		User existing = getById(userId);
		if (user.getEmail() != null) {
			checkEmailNotTaken(user.getEmail(), userId);
			existing.setEmail(user.getEmail());
		}
		if (user.getName() != null) {
			existing.setName(user.getName());
		}
		return userStorage.save(existing);
	}

	@Override
	public User getById(long userId) {
		return userStorage.findById(userId)
				.orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
	}

	@Override
	public List<User> getAll() {
		return userStorage.findAll();
	}

	@Override
	public void delete(long userId) {
		userStorage.deleteById(userId);
	}

	private void checkEmailNotTaken(String email, Long currentUserId) {
		userStorage.findByEmail(email)
				.filter(user -> !user.getId().equals(currentUserId))
				.ifPresent(user -> {
					throw new ConflictException("Email is already in use");
				});
	}
}
