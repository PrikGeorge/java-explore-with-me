package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.service.EventCommentService;
import ru.practicum.service.EventService;
import ru.practicum.service.ParticipationRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class PrivateController {

    private final ParticipationRequestService requestService;
    private final EventService eventService;
    private final EventCommentService eventCommentService;

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(
            @PathVariable("userId") long userId,
            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return eventService.getEvents(userId, from, size);
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(
            @PathVariable("userId") long userId,
            @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByUser(
            @PathVariable("userId") long userId,
            @PathVariable("eventId") long eventId) {
        return eventService.getEventByUser(userId, eventId);
    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(
            @PathVariable("userId") long userId,
            @PathVariable("eventId") long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateRequest) {
        return eventService.updateEvent(userId, eventId, updateRequest);
    }

    @GetMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventParticipants(@PathVariable long userId,
                                                              @PathVariable long eventId) {
        return requestService.getEventParticipants(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult changeRequestStatus(@PathVariable long userId,
                                                              @PathVariable long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest request) {
        return requestService.changeRequestStatus(userId, eventId, request);
    }

    @GetMapping("/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getUserRequests(@PathVariable("userId") long userId) {
        return requestService.getUserRequests(userId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addParticipationRequest(
            @PathVariable("userId") long userId,
            @RequestParam("eventId") long eventId) {
        return requestService.createParticipationRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(
            @PathVariable("userId") long userId,
            @PathVariable("requestId") long requestId) {
        return requestService.cancelParticipationRequest(userId, requestId);
    }

    @GetMapping("/comment")
    @ResponseStatus(HttpStatus.OK)
    public List<EventCommentDTO> getCommentsByUser(@PathVariable Long userId,
                                                   @RequestParam(required = false, defaultValue = "0") Integer from,
                                                   @RequestParam(required = false, defaultValue = "10") Integer size,
                                                   @RequestParam(defaultValue = "false") Boolean asc) {

        return eventCommentService.getCommentsByUser(userId, asc, from, size);
    }

    @PostMapping("/comment/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public EventCommentDTO addComment(@PathVariable Long userId,
                                      @PathVariable Long eventId,
                                      @Valid @RequestBody EventCommentDTO comment) {
        return eventCommentService.addComment(userId, eventId, comment);
    }

    @PutMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public EventCommentDTO updateComment(@PathVariable Long userId,
                                         @PathVariable Long commentId,
                                         @RequestBody EventCommentDTO comment) {
        return eventCommentService.updateComment(userId, commentId, comment);
    }

    @DeleteMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByOwner(@PathVariable Long userId,
                                     @PathVariable Long commentId) {
        eventCommentService.deleteCommentByOwner(userId, commentId);
    }

}
