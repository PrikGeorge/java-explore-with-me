package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.model.ParticipationRequest;


@Component
public class ParticipationRequestMapper {

    public static ParticipationRequestDto toDto(ParticipationRequest request) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());

        if (request.getCreated() != null) {
            dto.setCreated(request.getCreated());
        }

        if (request.getEvent() != null) {
            dto.setEvent(request.getEvent().getId());
        }

        if (request.getRequester() != null) {
            dto.setRequester(request.getRequester().getId());
        }

        dto.setStatus(request.getStatus());

        return dto;
    }

}
