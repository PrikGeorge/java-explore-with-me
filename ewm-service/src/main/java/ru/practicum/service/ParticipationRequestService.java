package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ParticipationRequestService {

    private final EventService eventService;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final ParticipationRequestRepository repository;


    private ParticipationRequest findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request with id=" + id + " was not found"));
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventParticipants(long userId, long eventId) {

        Event event = eventService.findById(eventId);
        User user = userService.findById(userId);

        if (!event.getInitiator().getId().equals(user.getId())) {
            throw new ConflictException("The user does not own this event");
        }

        List<ParticipationRequest> participationRequests = repository.findAllByEventId(event.getId());

        if (participationRequests.isEmpty()) {
            return Collections.emptyList();
        }

        return participationRequests.stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {
        Event event = eventService.findById(eventId);

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ConflictException("The participant limit has been reached");
        }

//        List<ParticipationRequest> pendingRequests = repository.findByEventIdAndStatus(event.getId(), EventState.PENDING);
//        if (pendingRequests.isEmpty()) {
//            throw new ConflictException("No pending requests for this event");
//        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        List<ParticipationRequest> participationRequests = repository.findByIdIn(request.getRequestIds());

        for (ParticipationRequest participationRequest : participationRequests) {
            if (!participationRequest.getStatus().equals(EventState.PENDING)) {
                throw new ConflictException("The status of an application can only be changed in the pending state");
            }
            switch (request.getStatus()) {
                case CONFIRMED:
                    if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                        participationRequest.setStatus(EventState.CONFIRMED);
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                        confirmedRequests.add(ParticipationRequestMapper.toDto(participationRequest));
                    } else {
                        throw new ConflictException("The participant limit has been reached");
                    }
                    break;
                case CANCELED:
                    participationRequest.setStatus(EventState.CANCELED);
                    rejectedRequests.add(ParticipationRequestMapper.toDto(participationRequest));
                    break;
                case REJECTED:
                    participationRequest.setStatus(EventState.REJECTED);
                    rejectedRequests.add(ParticipationRequestMapper.toDto(participationRequest));
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported status: " + request.getStatus());
            }

            repository.save(participationRequest);
            eventRepository.save(event);

        }

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        User user = userService.findById(userId);
        return repository.findByRequesterId(user.getId())
                .stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto createParticipationRequest(long userId, long eventId) {
        User user = userService.findById(userId);

        Event event = eventService.findById(eventId);

        List<ParticipationRequest> doubleRequest = repository.findByRequesterId(userId);
        if (!doubleRequest.isEmpty()) {
            throw new ConflictException("Can't add a repeat request");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("The initiator of the event can't participate in his own event");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("You cannot participate in an unpublished event");
        }

        int requestCount = repository.findAllByEventIdAndStatus(eventId, EventState.CONFIRMED).size();

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= requestCount) {
            throw new ConflictException("The event has reached the participation request limit");
        }

        List<ParticipationRequest> existingRequests = repository.findAllByEventIdAndRequesterId(event.getId(), user.getId());
        if (!existingRequests.isEmpty()) {
            throw new ConflictException("Duplicate participation request");
        }

        ParticipationRequest newRequest = new ParticipationRequest();
        newRequest.setCreated(LocalDateTime.now());
        newRequest.setEvent(event);
        newRequest.setRequester(user);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            newRequest.setStatus(EventState.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        } else {
            newRequest.setStatus(EventState.PENDING);
        }

        repository.save(newRequest);
        eventService.save(event);

        return ParticipationRequestMapper.toDto(newRequest);
    }

    public ParticipationRequestDto cancelParticipationRequest(long userId, long requestId) {
        User user = userService.findById(userId);

        ParticipationRequest request = findById(requestId);

        if (!Objects.equals(request.getRequester().getId(), user.getId())) {
            throw new NotFoundException("Permission denied");
        }

        if (request.getStatus() != EventState.CANCELED) {
            request.setStatus(EventState.CANCELED);
            request = repository.save(request);
        }

        return ParticipationRequestMapper.toDto(request);
    }

}
