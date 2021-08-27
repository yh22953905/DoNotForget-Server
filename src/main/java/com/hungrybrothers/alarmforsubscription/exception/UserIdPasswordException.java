package com.hungrybrothers.alarmforsubscription.exception;

import com.hungrybrothers.alarmforsubscription.common.Const;
import org.springframework.security.core.AuthenticationException;

public class UserIdPasswordException extends AuthenticationException {
    public UserIdPasswordException() {
        super(Const.ERROR_MESSAGE_AUTHENTICATION_FAIL);
    }
}
