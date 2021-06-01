package com.hungrybrothers.alarmforsubscription.subscription;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.hungrybrothers.alarmforsubscription.common.AuditEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Subscription extends AuditEntity {
    @Id @GeneratedValue
    private Long id;

    private String url;

    private Long cycle;

    private LocalDateTime nextReminderDateTime;
}
