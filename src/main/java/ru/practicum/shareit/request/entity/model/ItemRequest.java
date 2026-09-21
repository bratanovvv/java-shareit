package ru.practicum.shareit.request.entity.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.entity.model.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

	private Long id;
	private String description;
	private User requestor;
	private LocalDateTime created;
}
