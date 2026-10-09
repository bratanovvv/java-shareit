package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.validation.group.Create;
import ru.practicum.shareit.utils.validation.group.Update;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

	private final UserClient userClient;

	@PostMapping
	public ResponseEntity<UserDto> createUser(@RequestBody @Validated(Create.class) UserDto userDto) {
		log.info("Creating user {}", userDto);
		return userClient.createUser(userDto);
	}

	@PatchMapping("/{userId}")
	public ResponseEntity<UserDto> updateUser(@PathVariable long userId,
											  @RequestBody @Validated(Update.class) UserDto userDto) {
		log.info("Updating user {}", userId);
		return userClient.updateUser(userId, userDto);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<UserDto> getUser(@PathVariable long userId) {
		log.info("Getting user {}", userId);
		return userClient.getUser(userId);
	}

	@GetMapping
	public ResponseEntity<UserDto[]> getUsers() {
		log.info("Getting all users");
		return userClient.getUsers();
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable long userId) {
		log.info("Deleting user {}", userId);
		return userClient.deleteUser(userId);
	}
}
