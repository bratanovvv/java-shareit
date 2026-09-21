package ru.practicum.shareit.utils.mapper;

public interface Mapper<E, D> {

	D toDto(E entity);

	E toEntity(D dto);
}