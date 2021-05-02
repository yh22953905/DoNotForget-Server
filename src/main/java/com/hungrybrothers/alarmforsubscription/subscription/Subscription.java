package com.hungrybrothers.alarmforsubscription.subscription;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Subscription {

    @Id @GeneratedValue
    private Long id;

    private String url;

    private Long cycle;

    private LocalDateTime nextReminderDateTime;

}
