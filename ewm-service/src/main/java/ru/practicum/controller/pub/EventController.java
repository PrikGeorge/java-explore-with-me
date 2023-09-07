package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventParametersDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/events")
public class EventController {

    private final EventService service;
    private final StatsClient statsService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(HttpServletRequest request,
                                         @Valid @ModelAttribute EventParametersDto parametersDto) {
        statsService.addStatistic(new EndpointHitDto("main-service", "/events", request.getRemoteAddr(), LocalDateTime.now()));
        return service.getEventsByFilter(parametersDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(HttpServletRequest request,
                                 @PathVariable("id") Long id) {
        statsService.addStatistic(new EndpointHitDto("main-service", "/events/" + id, request.getRemoteAddr(), LocalDateTime.now()));
        return service.getEvent(id);
    }

}
