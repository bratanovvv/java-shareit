package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.utils.exception.errors.impl.ConflictException;
import ru.practicum.shareit.utils.exception.errors.impl.NotFoundException;
import ru.practicum.shareit.user.entity.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public User create(User user) {
		checkEmailNotTaken(user.getEmail(), null);
		return userRepository.save(user);
	}

	@Override
	@Transactional
	public User update(long userId, User user) {
		User existing = getById(userId);
		if (user.getEmail() != null) {
			checkEmailNotTaken(user.getEmail(), userId);
			existing.setEmail(user.getEmail());
		}
		if (user.getName() != null) {
			existing.setName(user.getName());
		}
		return userRepository.save(existing);
	}

	@Override
	@Transactional(readOnly = true)
	public User getById(long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getAll() {
		return userRepository.findAll();
	}

	@Override
	@Transactional
	public void delete(long userId) {
		if (userRepository.existsById(userId)) {
			userRepository.deleteById(userId);
		}
	}

	private void checkEmailNotTaken(String email, Long currentUserId) {
		userRepository.findByEmail(email)
				.filter(user -> !user.getId().equals(currentUserId))
				.ifPresent(user -> {
					throw new ConflictException("Email is already in use");
				});
	}
}
