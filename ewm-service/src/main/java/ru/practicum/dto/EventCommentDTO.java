package ru.practicum.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Builder
public class EventCommentDTO {
    private Long id;
    private Long eventId;
    private Long authorId;

    @Size(max = 1000)
    private String comment;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
