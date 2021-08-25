package com.hungrybrothers.alarmforsubscription.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class SignRequest {
    // TODO Validation
    private String userId;

    private String username;

    private String password;

    private String accountRole;
}
