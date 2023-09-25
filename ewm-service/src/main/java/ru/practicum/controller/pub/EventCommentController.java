package ru.practicum.controller.pub;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventCommentDTO;
import ru.practicum.service.EventCommentService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/comments")
public class EventCommentController {

    private final EventCommentService service;

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public EventCommentDTO getCommentById(@PathVariable Long commentId) {
        return service.getCommentById(commentId);
    }

    @GetMapping("/all/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<EventCommentDTO> getAllCommentsByEvent(@PathVariable Long eventId) {
        return service.getCommentsByEvent(eventId);
    }

}
