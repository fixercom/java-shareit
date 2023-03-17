package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    Comment toComment(CommentDtoRequest commentDtoRequest, Item item, User author, LocalDateTime created);

    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDtoResponse toCommentDtoResponse(Comment comment);

    List<CommentDtoResponse> toCommentDtoResponseList(List<Comment> comments);
}
