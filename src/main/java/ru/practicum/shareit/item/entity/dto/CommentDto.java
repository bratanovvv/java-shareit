package ru.practicum.shareit.item.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.utils.validation.group.Create;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

	private Long id;

	@NotBlank(groups = Create.class, message = "Comment text is required")
	private String text;

	private String authorName;

	private LocalDateTime created;
}
