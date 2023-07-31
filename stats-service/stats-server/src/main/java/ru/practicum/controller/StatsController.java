package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<HitDto> save(@RequestBody HitDto hitDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(statsService.save(hitDto));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatsDto>> getStatistic(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(name = "uris", defaultValue = "") List<String> uris,
            @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {

        return ResponseEntity.ok(statsService.getStatistics(start, end, uris, unique));
    }
}
