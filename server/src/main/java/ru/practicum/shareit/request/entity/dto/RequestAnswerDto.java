package ru.practicum.shareit.request.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestAnswerDto {

	private Long id;

	private String name;

	private Long ownerId;
}
