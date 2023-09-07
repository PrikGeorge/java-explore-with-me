package ru.practicum.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public class EventSpecifications {

    public static Specification<Event> hasUserIn(List<Long> users) {
        return (root, query, criteriaBuilder) -> {
            if (users != null && !users.isEmpty()) {
                return root.get("initiator").get("id").in(users);
            }
            return null;
        };
    }

    public static Specification<Event> hasStateIn(List<EventState> states) {
        return (root, query, criteriaBuilder) -> {
            if (states != null && !states.isEmpty()) {
                return criteriaBuilder.in(root.get("state")).value(states);
            }
            return null;
        };
    }

    public static Specification<Event> hasCategoryIn(List<Long> categories) {
        return (root, query, criteriaBuilder) -> {
            if (categories != null && !categories.isEmpty()) {
                return root.get("category").get("id").in(categories);
            }
            return null;
        };
    }

    public static Specification<Event> hasEventDateUntil(LocalDateTime end) {
        return (root, query, criteriaBuilder) -> {
            if (end != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), end);
            }
            return null;
        };
    }

    public static Specification<Event> hasEventDateFrom(LocalDateTime start) {
        return (root, query, criteriaBuilder) -> {
            if (start != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), start);
            }
            return null;
        };
    }


}
