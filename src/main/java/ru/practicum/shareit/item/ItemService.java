package ru.practicum.shareit.item;

import ru.practicum.shareit.item.entity.model.Comment;
import ru.practicum.shareit.item.entity.model.Item;

import java.util.List;

public interface ItemService {

	Item create(long userId, Item item);

	Item update(long userId, long itemId, Item patch);

	Item getById(long itemId);

	Item getById(long userId, long itemId);

	List<Item> getByOwner(long userId);

	List<Item> search(String text);

	Comment addComment(long userId, long itemId, Comment comment);
}
