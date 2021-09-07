package com.hungrybrothers.alarmforsubscription.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponse {
    private String jwtToken;

    private String refreshToken;
}
