package ru.practicum.shareit.user;

import ru.practicum.shareit.user.entity.model.User;

import java.util.List;

public interface UserService {

	User create(User user);

	User update(long userId, User user);

	User getById(long userId);

	List<User> getAll();

	void delete(long userId);
}
