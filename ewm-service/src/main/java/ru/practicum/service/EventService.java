package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.*;
import ru.practicum.exception.CannotBeModifiedException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.NotValidException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.*;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.specification.EventSpecifications;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository repository;
    private final ParticipationRequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final StatsClient statsClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Event findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));
    }

    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsByAdminFilter2(EventsFilterDto filterDto) {

        if (filterDto.getFrom() == null) {
            filterDto.setFrom(0);
        }

        if (filterDto.getSize() == null) {
            filterDto.setSize(10);
        }

        LocalDateTime start = LocalDateTime.now().minusYears(20);
        LocalDateTime end = LocalDateTime.now().plusYears(100);

        if (filterDto.getRangeStart() != null && filterDto.getRangeEnd() != null) {
            start = LocalDateTime.parse(filterDto.getRangeStart(), formatter);
            end = LocalDateTime.parse(filterDto.getRangeEnd(), formatter);
        }

        Specification<Event> spec = Specification.where(
                EventSpecifications.hasUserIn(filterDto.getUsers())
                        .and(EventSpecifications.hasStateIn(filterDto.getStates()))
                        .and(EventSpecifications.hasCategoryIn(filterDto.getCategories()))
                        .and(EventSpecifications.hasEventDateUntil(end))
                        .and(EventSpecifications.hasEventDateFrom(start))
        );

        Pageable pageable = PageRequest.of(filterDto.getFrom(), filterDto.getSize());

        List<Event> events = repository.findAll(spec, pageable).getContent();

        return events.stream().map(EventMapper::toFullDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsByAdminFilter(EventsFilterDto filterDto) {

        if (filterDto.getFrom() == null) {
            filterDto.setFrom(0);
        }

        if (filterDto.getSize() == null) {
            filterDto.setSize(10);
        }

        int page = filterDto.getFrom() / filterDto.getSize(); // Вычисление номера страницы
        Pageable pageable = PageRequest.of(page, filterDto.getSize());

        LocalDateTime start = LocalDateTime.now().minusYears(20);
        LocalDateTime end = LocalDateTime.now().plusYears(100);

        if (filterDto.getRangeStart() != null && filterDto.getRangeEnd() != null) {
            start = LocalDateTime.parse(filterDto.getRangeStart(), formatter);
            end = LocalDateTime.parse(filterDto.getRangeEnd(), formatter);
        }

        List<EventState> stateList;
        if (filterDto.getStates() != null) {
            stateList = new ArrayList<>(filterDto.getStates());
        } else {
            stateList = null;
        }

        Specification<Event> spec = Specification.where(
                EventSpecifications.hasUserIn(filterDto.getUsers())
                        .and(EventSpecifications.hasStateIn(stateList))
                        .and(EventSpecifications.hasCategoryIn(filterDto.getCategories()))
                        .and(EventSpecifications.hasEventDateFrom(start))
                        .and(EventSpecifications.hasEventDateUntil(end))
        );

        List<Event> events = repository.findAll(spec, pageable).getContent();

        // Логика по обработке запросов и статистики просмотров
        List<Long> eventIdList = new ArrayList<>();
        for (Event event : events) {
            eventIdList.add(event.getId());
        }

        List<RequestCountDto> requestCountDtoList = requestRepository.findRequestCountDtoListByEventId(eventIdList, EventState.CONFIRMED);
        Map<Long, Long> requestCountDtoMap = requestCountDtoList.stream()
                .collect(Collectors.toMap(RequestCountDto::getEventId, RequestCountDto::getRequestCount));

        List<ViewStatsDto> stat = statsClient.getStatistic(LocalDateTime.now().minusYears(20), LocalDateTime.now().plusYears(100),
                EventMapper.toUri(events), Boolean.TRUE);
        Map<String, ViewStatsDto> statsMap = stat.stream()
                .collect(Collectors.toMap(ViewStatsDto::getUri, statsHitDto -> statsHitDto));

        for (Event event : events) {
            if (!requestCountDtoMap.isEmpty()) {
                if (requestCountDtoMap.containsKey(event.getId())) {
                    event.setConfirmedRequests(requestCountDtoMap.get(event.getId()));
                }
            } else {
                event.setConfirmedRequests(0L);
            }
        }

        for (Event event : events) {
            if (!statsMap.isEmpty()) {
                String uri = "/events/" + event.getId();
                if (statsMap.containsKey(uri)) {
                    event.setViews(statsMap.get(uri).getHits());
                }
            } else {
                event.setViews(0L);
            }
        }

        // Преобразование в полный DTO, включая запросы и статистику
//        return EventMapper.toFullDto(events);
        return events.stream().map(EventMapper::toFullDto).collect(Collectors.toList());
    }


    @Transactional
    public Boolean findFirstByCategoryId(Long categoryId) {
        return Objects.nonNull(repository.findFirstByCategoryId(categoryId));
    }

    @Transactional
    public List<Event> findAllById(List<Long> events) {
        return repository.findAllById(events);
    }

    public EventFullDto update(Long eventId, UpdateEventAdminRequest updateRequest) {
        Event event = findById(eventId);
        Category category = event.getCategory();
        if (updateRequest.getCategory() != null) {
            category = categoryRepository.findById(updateRequest.getCategory()).orElse(null);
        }

        if (updateRequest.getEventDate() != null && updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new NotValidException("Event start date should not be earlier than one hour from the publication date");
        }

        if (updateRequest.getStateAction() != null) {
            if ((event.getState() != EventState.PENDING) && (updateRequest.getStateAction() == StateAction.PUBLISH_EVENT)) {
                throw new ConflictException("Cannot publish the event because it's not in the right state: PUBLISHED");
            }

            if ((event.getState() == EventState.PUBLISHED) && (updateRequest.getStateAction() == StateAction.REJECT_EVENT)) {
                throw new ConflictException("Cannot reject the event because it's already published");
            }

            switch (updateRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid State Action");
            }
        }

        EventMapper.updateEventFromRequest(event, updateRequest, category);
        return EventMapper.toFullDto(repository.save(event));
    }

    @Transactional
    public Event save(Event event) {
        return repository.save(event);
    }

    @Transactional
    public EventFullDto getEventByUser(Long userId, Long eventId) {

        User user = userService.findById(userId);

        Event event = repository.findOne(
                        Specification.where(EventSpecifications.hasUserIn(Collections.singletonList(user.getId())))
                                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), eventId)))
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found or not initiated by current user"));

        return EventMapper.toFullDto(event);

    }

    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User user = userService.findById(userId);
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElse(null);

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new NotValidException("The event cannot be scheduled less than two hours from now");
        }

        Event event = EventMapper.toEntity(newEventDto);
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setCategory(category);
        event.setState(EventState.PENDING);

        return EventMapper.toFullDto(save(event));
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(Long userId, int from, int size) {
        User user = userService.findById(userId);

        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = repository.getEventsByInitiatorId(user.getId(), pageable);

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        return events.stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest updateRequest) {

        Event event = findById(eventId);

        User currentUser = userService.findById(userId);

        if (!event.getInitiator().getId().equals(currentUser.getId())) {
            throw new CannotBeModifiedException("User with id=" + userId + " is not the initiator of the event");
        }

        if (event.getState() != EventState.CANCELED && event.getState() != EventState.PENDING) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (updateRequest.getEventDate() != null && updateRequest.getEventDate().isBefore(LocalDateTime.now())) {
            throw new NotValidException("The updateRequest date and time cannot be earlier than the current moment");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new CannotBeModifiedException("The event date and time cannot be earlier than two hours from the current moment");
        }

        Optional.ofNullable(updateRequest.getStateAction()).ifPresent(stateAction -> {
            if (stateAction == StateAction.SEND_TO_REVIEW) {
                event.setState(EventState.PENDING);
            }
            if (stateAction == StateAction.CANCEL_REVIEW) {
                event.setState(EventState.CANCELED);
            }
        });

        return EventMapper.toFullDto(save(event));
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByFilter(EventParametersDto parametersDto) {

        if (parametersDto.getFrom() == null) {
            parametersDto.setFrom(0);
        }

        if (parametersDto.getSize() == null) {
            parametersDto.setSize(10);
        }

        if (parametersDto.getOnlyAvailable() == null) {
            parametersDto.setOnlyAvailable(false);
        }

        // Определение диапазона дат
        LocalDateTime rangeStart = parametersDto.getRangeStart() != null ?
                LocalDateTime.parse(parametersDto.getRangeStart(), formatter) : LocalDateTime.now();

        LocalDateTime rangeEnd = parametersDto.getRangeEnd() != null ?
                LocalDateTime.parse(parametersDto.getRangeEnd(), formatter) : LocalDateTime.now().plusYears(10);

        if (rangeEnd.isBefore(rangeStart) || rangeStart.isEqual(rangeEnd)) {
            throw new NotValidException("Date and time are not correct");
        }

        // Определение типа сортировки
        Sort sort = Sort.by(Sort.Direction.ASC, "eventDate");
        if (parametersDto.getSort() != null) {
            switch (parametersDto.getSort()) {
                case EVENT_DATE:
                    sort = Sort.by(Sort.Direction.ASC, "eventDate");
                    break;
                case VIEWS:
                    sort = Sort.by(Sort.Direction.DESC, "views");
                    break;
                default:
                    break;
            }
        }
        Pageable pageable = PageRequest.of(parametersDto.getFrom(), parametersDto.getSize(), sort);

        Specification<Event> spec = Specification.where(
                EventSpecifications.hasStateIn(Collections.singletonList(EventState.PUBLISHED))// только опубликованные события
                        .and(EventSpecifications.hasCategoryIn(parametersDto.getCategories())) // категории
                        .and(EventSpecifications.hasEventDateFrom(rangeStart)) // диапазон дат
                        .and(EventSpecifications.hasEventDateUntil(rangeEnd)) // диапазон дат
        );

        if (parametersDto.getText() != null && !parametersDto.getText().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")),
                            "%" + parametersDto.getText().toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                            "%" + parametersDto.getText().toLowerCase() + "%")
            ));
        }

        if (parametersDto.getPaid() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("paid"), parametersDto.getPaid()));
        }

        List<Event> events = repository.findAll(spec, pageable).getContent();

        List<ViewStatsDto> viewStats = statsClient.getStatistic(
                LocalDateTime.now().minusYears(20),
                LocalDateTime.now().plusYears(100),
                EventMapper.toUri(events),
                Boolean.TRUE
        );

        // Преобразование списка статистики в карту
        Map<String, Long> viewsMap = viewStats.stream()
                .collect(Collectors.toMap(ViewStatsDto::getUri, ViewStatsDto::getHits));

        return events.stream()
                .map(event -> {
                    EventShortDto dto = EventMapper.toShortDto(event);
                    String uri = "/events/" + event.getId();
                    dto.setViews(viewsMap.getOrDefault(uri, 0L));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventFullDto getEvent(Long id) {
        Event event = findById(id);

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event not published");
        }

        EventFullDto eventFullDto = EventMapper.toFullDto(event);


        List<ViewStatsDto> viewStatsDtos = statsClient.getStatistic(LocalDateTime.now().minusYears(20), LocalDateTime.now().plusYears(100),
                Collections.singleton("/events/" + id), Boolean.TRUE);

        if (!viewStatsDtos.isEmpty()) {
            eventFullDto.setViews(viewStatsDtos.get(0).getHits());
        }

        return eventFullDto;
    }
}
