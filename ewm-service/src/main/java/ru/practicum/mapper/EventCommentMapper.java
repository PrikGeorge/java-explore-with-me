package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.EventCommentDTO;
import ru.practicum.model.EventComment;

@Component
public class EventCommentMapper {

    public static EventCommentDTO toDTO(EventComment comment) {
        return EventCommentDTO.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .authorId(comment.getAuthor().getId())
                .eventId(comment.getEvent().getId())
                .comment(comment.getComment())
                .build();
    }

    public static EventComment toEntity(EventCommentDTO comment) {

        return EventComment.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .comment(comment.getComment())
                .build();
    }
}
