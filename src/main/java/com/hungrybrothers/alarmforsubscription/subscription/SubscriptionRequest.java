package com.hungrybrothers.alarmforsubscription.subscription;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hungrybrothers.alarmforsubscription.common.Const;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {
    private String url;

    private Long cycle;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Const.LOCAL_DATE_TIME_FORMAT, timezone = "Asia/Seoul")
    private LocalDateTime nextReminderDateTime;
}
