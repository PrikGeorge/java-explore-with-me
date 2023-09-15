package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.RequestCountDto;
import ru.practicum.model.EventState;
import ru.practicum.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long>, JpaSpecificationExecutor<ParticipationRequest> {

    List<ParticipationRequest> findAllByEventIdAndRequesterId(Long eventId, Long userId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findByRequesterId(Long id);

    List<ParticipationRequest> findAllByEventIdAndStatus(long eventId, EventState status);

    List<ParticipationRequest> findByIdIn(List<Long> requestIds);

    @Query("select new ru.practicum.dto.RequestCountDto(r.event.id, count(r.id)) " +
            "from ParticipationRequest as r " +
            "where r.event.id IN ?1 " +
            "AND r.status = ?2 " +
            "group by r.event.id " +
            "order by count(r.id) desc")
    List<RequestCountDto> findRequestCountDtoListByEventId(List<Long> eventIdList, EventState status);

}
