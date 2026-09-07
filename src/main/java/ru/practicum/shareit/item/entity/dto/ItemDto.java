package ru.practicum.shareit.item.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ItemDto {

	private Long id;

	@NotBlank(groups = Create.class, message = "Item name is required")
	private String name;

	@NotBlank(groups = Create.class, message = "Item description is required")
	private String description;

	@NotNull(groups = Create.class, message = "Item availability is required")
	private Boolean available;

	private Long requestId;
}
