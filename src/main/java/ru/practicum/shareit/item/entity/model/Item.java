package ru.practicum.shareit.item.entity.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.request.entity.model.ItemRequest;
import ru.practicum.shareit.user.entity.model.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {

	private Long id;
	private String name;
	private String description;
	private Boolean available;
	private User owner;
	private ItemRequest request;
}
