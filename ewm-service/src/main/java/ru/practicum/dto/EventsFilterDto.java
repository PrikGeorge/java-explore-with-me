package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.model.EventState;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Data
@AllArgsConstructor
public class EventsFilterDto {
    private List<Long> users;
    private List<EventState> states;
    private List<Long> categories;

    private String rangeStart;
    private String rangeEnd;

    @PositiveOrZero
    private Integer from;

    @Positive
    private Integer size;
}
