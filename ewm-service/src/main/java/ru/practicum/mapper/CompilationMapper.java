package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.CompilationDto;
import ru.practicum.model.Compilation;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class CompilationMapper {
    public CompilationDto toDto(Compilation compilation) {
        CompilationDto dto = new CompilationDto();

        dto.setId(compilation.getId());
        dto.setPinned(compilation.getPinned() != null);
        dto.setTitle(compilation.getTitle());

        if (!compilation.getEvents().isEmpty()) {
            dto.setEvents(compilation.getEvents().stream()
                    .map(EventMapper::toShortDto)
                    .collect(Collectors.toList()));

        } else {
            dto.setEvents(new ArrayList<>());
        }

        return dto;
    }

}
