package com.hungrybrothers.alarmforsubscription.exception;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST.value(), "C001", "잘못된 요청입니다. 관리자에게 문의하세요."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(), "C002", "비정상 요청입니다. 관리자에게 문의하세요."),
    ENTITY_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "C003", "요청하신 사항을 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "C004", "서버 오류입니다. 관리자에게 문의하세요."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST.value(), "C005", "잘못된 요청입니다. 관리자에게 문의하세요."),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "C006", "권한이 없습니다. 관리자에게 문의하세요."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "C007", "인증 토큰이 만료되었습니다. 다시 로그인 해주세요."),
    MAIL_EXCEPTION(HttpStatus.BAD_REQUEST.value(), "C008", "이메일 주소가 유효하지 않습니다. 이메일 주소를 확인해주세요."),
    VERIFY_CODE_EXCEPTION(HttpStatus.BAD_REQUEST.value(), "C009", "인증 코드가 유효하지 않습니다. 인증 코드를 확인해주세요."),
    ACCOUNT_EXISTS(HttpStatus.BAD_REQUEST.value(), "C010", "이미 존재하는 아이디입니다. 이메일을 확인해주세요."),
    ;

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return this.message;
    }
}
