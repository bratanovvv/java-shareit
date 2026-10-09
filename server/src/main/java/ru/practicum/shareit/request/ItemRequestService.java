package ru.practicum.shareit.request;

import ru.practicum.shareit.request.entity.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

	ItemRequest create(long userId, ItemRequest request);

	List<ItemRequest> getByRequestor(long userId);

	List<ItemRequest> getByOthers(long userId);

	ItemRequest getById(long userId, long requestId);
}
