package ru.practicum.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.exception.InvalidTimeParameterException;
import ru.practicum.mapper.HitMapper;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.Hit;
import ru.practicum.model.Stats;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    public StatsServiceImpl(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    public HitDto save(HitDto hitDto) {
        Hit hit = HitMapper.toHit(hitDto);
        Hit savedEntry = statsRepository.save(hit);
        return HitMapper.toHitDto(savedEntry);
    }

    @Override
    public List<StatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (end.isBefore(start)) {
            throw new InvalidTimeParameterException("Время окончания не может быть раньше времени начала");
        }

        uris = uris == null ? new ArrayList<>() : uris;

        return Boolean.TRUE.equals(unique) ?
                getStatisticsWithUniqueIp(start, end, uris).stream()
                        .map(StatsMapper::toStatsDto)
                        .collect(Collectors.toList()) :
                getAllStatistics(start, end, uris).stream()
                        .map(StatsMapper::toStatsDto)
                        .collect(Collectors.toList());
    }

    private List<Stats> getStatisticsWithUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return uris.isEmpty() ? statsRepository.getStatisticsWithUniqueIp(start, end) : statsRepository.getStatisticsWithUniqueIpAndUris(start, end, uris);

    }

    private List<Stats> getAllStatistics(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return uris.isEmpty() ? statsRepository.getAllStatistics(start, end) : statsRepository.getAllStatisticsWithUris(start, end, uris);

    }

}
