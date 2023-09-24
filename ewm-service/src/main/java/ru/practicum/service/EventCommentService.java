package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventCommentDTO;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.NotValidException;
import ru.practicum.mapper.EventCommentMapper;
import ru.practicum.model.Event;
import ru.practicum.model.EventComment;
import ru.practicum.model.User;
import ru.practicum.repository.EventCommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventCommentService {

    private final EventCommentRepository repository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public EventCommentDTO addComment(Long userId, Long eventId, EventCommentDTO entryDto) {
        User user = getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);

        EventComment comment = EventCommentMapper.toEntity(entryDto);
        comment.setEvent(event);
        comment.setAuthor(user);
        return EventCommentMapper.toDTO(repository.save(comment));
    }

    public EventCommentDTO updateComment(Long userId, Long commentId, EventCommentDTO entryDto) {
        EventComment comment = getCommentOrThrow(commentId);
        validateOwnership(comment.getAuthor().getId(), userId, "Only sender can update!");

        Optional.ofNullable(entryDto.getComment())
                .ifPresent(newComment -> {
                    comment.setComment(newComment);
                    comment.setUpdatedAt(LocalDateTime.now());
                });

        return EventCommentMapper.toDTO(repository.save(comment));
    }

    public List<EventCommentDTO> getCommentsByUser(Long userId, Boolean asc, Integer from, Integer size) {
        User user = getUserOrThrow(userId);

        Pageable pageable = PageRequest.of(from, size);
        List<EventComment> comments = asc
                ? repository.getAllByAuthorIdOrderByCreatedAtAsc(user.getId(), pageable)
                : repository.getAllByAuthorIdOrderByCreatedAtDesc(user.getId(), pageable);

        return comments.stream()
                .map(EventCommentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteCommentByOwner(Long userId, Long commentId) {
        EventComment comment = getCommentOrThrow(commentId);
        validateOwnership(comment.getAuthor().getId(), userId, "Only sender or admin can delete it!");

        repository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    public EventCommentDTO getCommentById(Long commentId) {
        EventComment comment = getCommentOrThrow(commentId);
        return EventCommentMapper.toDTO(comment);
    }

    @Transactional(readOnly = true)
    public List<EventCommentDTO> getCommentsByEvent(Long eventId) {
        List<EventComment> comments = repository.getAllByEventIdOrderByCreatedAtAsc(eventId);

        return comments.stream()
                .map(EventCommentMapper::toDTO)
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found! userId = " + userId));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found! eventId = " + eventId));
    }

    private EventComment getCommentOrThrow(Long commentId) {
        return repository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found! commentId = " + commentId));
    }

    private void validateOwnership(Long ownerId, Long userId, String errorMessage) {
        if (!Objects.equals(ownerId, userId)) {
            throw new NotValidException(errorMessage);
        }
    }

}
