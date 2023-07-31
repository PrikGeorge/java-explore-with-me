package ru.practicum.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HitDto {
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime created;

}
