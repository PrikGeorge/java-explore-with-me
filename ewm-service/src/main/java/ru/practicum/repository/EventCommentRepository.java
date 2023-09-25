package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.EventComment;

import java.util.List;

public interface EventCommentRepository extends JpaRepository<EventComment, Long> {
    List<EventComment> getAllByAuthorIdOrderByCreatedAtAsc(Long id, Pageable pageable);

    List<EventComment> getAllByEventIdOrderByCreatedAtAsc(Long id);

    List<EventComment> getAllByAuthorIdOrderByCreatedAtDesc(Long id, Pageable pageable);
}
