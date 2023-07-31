package ru.practicum.mapper;

import ru.practicum.dto.StatsDto;
import ru.practicum.model.Stats;

import java.util.Objects;

public class StatsMapper {

    public static StatsDto toStatsDto(Stats stats) {
        if (Objects.isNull(stats)) {
            return null;
        }

        return StatsDto.builder()
                .app(stats.getApp())
                .uri(stats.getUri())
                .hits(stats.getHits())
                .build();
    }
}
