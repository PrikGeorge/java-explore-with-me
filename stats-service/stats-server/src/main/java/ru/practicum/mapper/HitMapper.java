package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.HitDto;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class HitMapper {

    public static Hit toHit(HitDto hitDto) {
        if (Objects.isNull(hitDto)) {
            return null;
        }

        return Hit.builder()
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .created(Objects.nonNull(hitDto.getCreated()) ? hitDto.getCreated() : LocalDateTime.now())
                .build();
    }

    public static HitDto toHitDto(Hit hit) {
        if (hit == null) {
            return null;
        }

        HitDto hitDto = new HitDto();

        hitDto.setApp(hit.getApp());
        hitDto.setUri(hit.getUri());
        hitDto.setIp(hit.getIp());

        if (hit.getCreated() != null) {
            hitDto.setCreated(hit.getCreated());
        }

        return hitDto;
    }
}
