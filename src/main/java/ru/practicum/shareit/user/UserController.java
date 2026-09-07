package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.entity.UserMapper;
import ru.practicum.shareit.user.entity.dto.UserDto;
import ru.practicum.shareit.user.entity.model.User;
import ru.practicum.shareit.utils.validation.Create;
import ru.practicum.shareit.utils.validation.Update;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	public UserDto create(@RequestBody @Validated(Create.class) UserDto userDto) {
		log.info("Creating user");
		User created = userService.create(UserMapper.toUser(userDto));
		return UserMapper.toUserDto(created);
	}

	@PatchMapping("/{userId}")
	public UserDto update(@PathVariable long userId, @RequestBody @Validated(Update.class) UserDto userDto) {
		log.info("Updating user {}", userId);
		User updated = userService.update(userId, UserMapper.toUser(userDto));
		return UserMapper.toUserDto(updated);
	}

	@GetMapping("/{userId}")
	public UserDto getById(@PathVariable long userId) {
		log.info("Getting user {}", userId);
		return UserMapper.toUserDto(userService.getById(userId));
	}

	@GetMapping
	public List<UserDto> getAll() {
		log.info("Getting all users");
		return userService.getAll().stream()
				.map(UserMapper::toUserDto)
				.toList();
	}

	@DeleteMapping("/{userId}")
	public void delete(@PathVariable long userId) {
		log.info("Deleting user {}", userId);
		userService.delete(userId);
	}
}
