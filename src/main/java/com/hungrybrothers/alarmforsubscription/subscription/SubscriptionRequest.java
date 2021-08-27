package com.hungrybrothers.alarmforsubscription.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {
    private Long id;

    private String url;

    private Long cycle;

    private LocalDateTime nextReminderDateTime;
}
