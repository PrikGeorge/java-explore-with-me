package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
public class EventParametersDto {

    @Size(min = 1, max = 7000)
    private String text;

    private List<Long> categories;
    private Boolean paid;
    private String rangeStart;
    private String rangeEnd;
    private Boolean onlyAvailable;

    private SortType sort;

    @PositiveOrZero
    private Integer from;

    @Positive
    private Integer size;

}
