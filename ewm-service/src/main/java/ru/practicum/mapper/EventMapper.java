package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.*;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;

import java.util.*;

@Component
public class EventMapper {


    public static EventFullDto toFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(event.getId());
        eventFullDto.setInitiator(UserMapper.toShortDto(event.getInitiator()));
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setCategory(CategoryMapper.toDto(event.getCategory()));
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setLocation(new Location(event.getLat(), event.getLon()));
        eventFullDto.setPaid(event.getPaid() != null && event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setRequestModeration(event.getRequestModeration() == null || event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests() == null ? 0 : event.getConfirmedRequests());
        eventFullDto.setViews(event.getViews());

        return eventFullDto;
    }


    public static Event toEntity(NewEventDto dto) {
        Event event = new Event();
        event.setAnnotation(dto.getAnnotation());

        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());

        if (dto.getLocation() != null) {
            event.setLat(dto.getLocation().getLat());
            event.setLon(dto.getLocation().getLon());
        }

        event.setPaid(dto.getPaid());
        event.setParticipantLimit(dto.getParticipantLimit() == null ? 0 : dto.getParticipantLimit());
        event.setRequestModeration(dto.getRequestModeration());
        event.setTitle(dto.getTitle());

        return event;
    }

    public static EventShortDto toShortDto(Event event) {
        EventShortDto dto = new EventShortDto();

        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setPaid(event.getPaid());
        dto.setTitle(event.getTitle());
        dto.setViews(event.getViews());

        Optional.ofNullable(event.getConfirmedRequests()).ifPresent(dto::setConfirmedRequests);
        Optional.ofNullable(event.getEventDate()).ifPresent(dto::setEventDate);

        if (Objects.nonNull(event.getCategory())) {
            dto.setCategory(CategoryMapper.toDto(event.getCategory()));
        }

        if (Objects.nonNull(event.getInitiator())) {
            dto.setInitiator(UserMapper.toDto(event.getInitiator()));
        }

        return dto;
    }

    public static Event toEntity(UpdateEventUserRequest updateRequest) {
        Event event = new Event();
        event.setAnnotation(updateRequest.getAnnotation());

        event.setDescription(updateRequest.getDescription());
        event.setEventDate(updateRequest.getEventDate());

        if (updateRequest.getLocation() != null) {
            event.setLat(updateRequest.getLocation().getLat());
            event.setLon(updateRequest.getLocation().getLon());
        }

        event.setPaid(updateRequest.getPaid());
        event.setParticipantLimit(updateRequest.getParticipantLimit());
        event.setPublishedOn(updateRequest.getPublishedOn());
        event.setRequestModeration(updateRequest.getRequestModeration());

        event.setTitle(updateRequest.getTitle());

        return event;
    }

    public static void updateEventFromRequest(Event event, UpdateEventAdminRequest request, Category category) {
        Optional.ofNullable(request.getAnnotation()).ifPresent(event::setAnnotation);
        event.setCategory(category);
        Optional.ofNullable(request.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(request.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(request.getLocation()).ifPresent(location -> {
            event.setLat(location.getLat());
            event.setLon(location.getLon());
        });
        Optional.ofNullable(request.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(request.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(request.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(request.getTitle()).ifPresent(event::setTitle);

    }

    public static List<String> toUri(Collection<Event> eventCollection) {
        List<String> uriList = new ArrayList<>();
        for (Event e : eventCollection) {
            uriList.add("/events/".concat(e.getId().toString()));
        }
        return uriList;
    }
}
