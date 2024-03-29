package ru.practicum.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class UpdateCompilationDto {

    @Size(min = 1, max = 50)
    private String title;

    private Boolean pinned;

    private List<Long> events;

}
