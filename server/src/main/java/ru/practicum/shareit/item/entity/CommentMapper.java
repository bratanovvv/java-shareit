package ru.practicum.shareit.item.entity;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.entity.dto.CommentDto;
import ru.practicum.shareit.item.entity.model.Comment;
import ru.practicum.shareit.utils.mapper.Mapper;

@Component
public final class CommentMapper implements Mapper<Comment, CommentDto> {

	@Override
	public CommentDto toDto(Comment comment) {
		return new CommentDto(
				comment.getId(),
				comment.getText(),
				comment.getAuthor() != null ? comment.getAuthor().getName() : null,
				comment.getCreated()
		);
	}

	@Override
	public Comment toEntity(CommentDto commentDto) {
		return new Comment(
				commentDto.getId(),
				commentDto.getText(),
				null,
				null,
				null
		);
	}
}
