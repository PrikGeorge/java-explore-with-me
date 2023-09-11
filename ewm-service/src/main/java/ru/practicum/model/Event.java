package ru.practicum.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(min = 20, max = 2000)
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private Long confirmedRequests;

    private LocalDateTime createdOn;

    @Size(min = 20, max = 7000)
    private String description;

    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User initiator;

    private Float lat;

    private Float lon;

    private Boolean paid;

    private Integer participantLimit;

    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private EventState state;

    @Size(min = 3, max = 120)
    private String title;

    private Long views;

    public Event() {
        this.confirmedRequests = 0L;
    }
}
