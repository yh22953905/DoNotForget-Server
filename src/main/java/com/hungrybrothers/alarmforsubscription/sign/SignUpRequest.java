package com.hungrybrothers.alarmforsubscription.sign;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

import org.springframework.validation.annotation.Validated;

import com.hungrybrothers.alarmforsubscription.common.Const;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class SignUpRequest {
    @Email(message = Const.VALID_MESSAGE_EMAIL)
    private String userId;

    @Pattern(regexp = "^[a-zA-z0-9가-힣]{4,18}$", message = Const.VALID_MESSAGE_USERNAME)
    private String username;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = Const.VALID_MESSAGE_PASSWORD)
    private String password;

    private String accountRole;
}
