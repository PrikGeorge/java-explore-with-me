package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventService eventService;

    private Compilation findById(Long id) {
        return compilationRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Category with id=" + id + " was not found"));
    }

    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).getContent();
        }
        return compilations.stream().map(compilationMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompilationDto get(Long compId) throws ChangeSetPersister.NotFoundException {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        return compilationMapper.toDto(compilation);
    }

    public CompilationDto save(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(newCompilationDto.getPinned());

        List<Event> events;
        if (newCompilationDto.getEvents() != null) {
            events = newCompilationDto.getEvents().stream()
                    .map(eventService::findById)
                    .collect(Collectors.toList());
        } else {
            events = new ArrayList<>();
        }
        compilation.setEvents(events);

        return compilationMapper.toDto(compilationRepository.save(compilation));

    }

    public void delete(Long id) {
        compilationRepository.delete(findById(id));
    }

    public CompilationDto update(Long id, UpdateCompilationDto updateCompilationDto) {

        Compilation compilation = findById(id);

        compilation.setTitle(updateCompilationDto.getTitle());
        compilation.setPinned(updateCompilationDto.getPinned());

        if (updateCompilationDto.getEvents() != null) {
            compilation.setEvents(eventService.findAllById(updateCompilationDto.getEvents()));
        }

        return compilationMapper.toDto(compilationRepository.save(compilation));
    }
}
