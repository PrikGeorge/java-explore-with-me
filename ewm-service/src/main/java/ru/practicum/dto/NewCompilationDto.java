package ru.practicum.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class NewCompilationDto {
    @NotBlank(message = "Title cannot be blank")
    @NotNull(message = "Title cannot be null")
    @Size(min = 1, max = 50, message = "Title must be between 1 and 50 characters")
    private String title;

    private Boolean pinned;

    private List<Long> events;

}
