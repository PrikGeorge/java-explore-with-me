package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Hit;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query(" select new ru.practicum.model.Stats(app, uri, count(distinct ip) as hits) from Hit " +
            "where created between :start and :end and uri in(:uri) " +
            "group by app, uri order by hits desc")
    List<Stats> getStatisticsWithUniqueIpAndUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uri") List<String> uri);

    @Query(" select new ru.practicum.model.Stats(app, uri, count(ip) as hits) from Hit " +
            "where created between :start and :end and uri in(:uri) " +
            "group by app, uri order by hits desc")
    List<Stats> getAllStatisticsWithUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uri") List<String> uri);

    @Query(" select new ru.practicum.model.Stats(app, uri, count(distinct ip) as hits) from Hit " +
            "where created between :start and :end " +
            "group by app, uri order by hits desc")
    List<Stats> getStatisticsWithUniqueIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(" select new ru.practicum.model.Stats(app, uri, count(ip) as hits) from Hit " +
            "where created between :start and :end " +
            "group by app, uri order by hits desc")
    List<Stats> getAllStatistics(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
