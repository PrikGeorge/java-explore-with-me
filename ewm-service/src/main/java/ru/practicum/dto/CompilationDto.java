package ru.practicum.dto;

import lombok.Data;

import java.util.List;

@Data
public class CompilationDto {
    private Long id;
    private Boolean pinned;
    private String title;
    private List<EventShortDto> events;
}
