package ru.practicum.shareit.utils.mapper;

import java.util.List;

public interface Mapper<E, D> {

	D toDto(E entity);

	E toEntity(D dto);

	default List<D> toDtoList(List<E> entities) {
		if (entities == null) {
			return List.of();
		}
		return entities.stream().map(this::toDto).toList();
	}
}