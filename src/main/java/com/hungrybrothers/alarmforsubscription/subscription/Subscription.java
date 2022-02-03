package com.hungrybrothers.alarmforsubscription.subscription;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.hungrybrothers.alarmforsubscription.common.AuditEntity;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Entity
public class Subscription extends AuditEntity {
    @Id @GeneratedValue
    private Long id;

    private String url;

    private Long cycle;

    private LocalDateTime nextReminderDateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Subscription that = (Subscription) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
