package ru.practicum.shareit.user.entity;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.entity.dto.UserDto;
import ru.practicum.shareit.user.entity.model.User;
import ru.practicum.shareit.utils.mapper.Mapper;

@Component
public final class UserMapper implements Mapper<User, UserDto> {

	@Override
	public UserDto toDto(User user) {
		return new UserDto(
				user.getId(),
				user.getName(),
				user.getEmail()
		);
	}

	@Override
	public User toEntity(UserDto userDto) {
		return new User(
				userDto.getId(),
				userDto.getName(),
				userDto.getEmail()
		);
	}
}