package com.hungrybrothers.alarmforsubscription.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    private Long id;

    private String url;

    private Long cycle;

    private String nextReminderDateTime;
}
