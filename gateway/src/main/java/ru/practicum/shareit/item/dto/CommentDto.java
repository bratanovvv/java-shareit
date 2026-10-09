package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

	private Long id;

	@NotBlank(message = "Comment text is required")
	private String text;

	private String authorName;

	private LocalDateTime created;
}
