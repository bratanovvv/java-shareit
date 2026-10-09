package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

	private Long id;

	@NotBlank(message = "Request description is required")
	private String description;

	private LocalDateTime created;

	private List<RequestAnswerDto> items;
}
