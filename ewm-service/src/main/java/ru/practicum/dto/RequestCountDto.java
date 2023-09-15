package ru.practicum.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RequestCountDto {

    private Long eventId;

    private Long requestCount;
}
