package ru.practicum.shareit.user.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.utils.validation.Create;
import ru.practicum.shareit.utils.validation.Update;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

	private Long id;

	@NotBlank(groups = Create.class, message = "User name is required")
	private String name;

	@NotBlank(groups = Create.class, message = "User email is required")
	@Email(groups = {Create.class, Update.class}, message = "Valid user email is required")
	private String email;
}
