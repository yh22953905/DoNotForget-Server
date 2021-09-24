package com.hungrybrothers.alarmforsubscription.common;

public class Const {
    public static final String API_SIGN = "/api/sign";
    public static final String API_SUBSCRIPTION = "/api/subscriptions";
    public static final String API_HATEOAS = "/api/hateoas";
    public static final String ERROR_URL = "/error";

    public static final long TIMEOUT = 30L;

    public static final String ERROR_MESSAGE_AUTHENTICATION_FAIL = "아이디와 비밀번호를 확인해주세요";

    public static final String LOG_MESSAGE_TOKEN_INVALID = "The token is invalid";
    public static final String LOG_MESSAGE_TOKEN_EXPIRED = "The token is expired";

    public static final String MAIL_SUBJECT = "[DO-NOT-FORGET] 이메일 인증 코드입니다.";
    public static final String MAIL_TEXT = "이메일 인증 코드는 '%s' 입니다.";
    public static final int MAIL_CODE_LENGTH = 6;

    public static final String VALID_MESSAGE_EMAIL = "이메일 형식에 맞게 작성해주세요.";
    public static final String VALID_MESSAGE_USERNAME = "닉네임은 네 글자 이상의 영어 대소문자, 숫자, 한글로 작성해주세요.";
    public static final String VALID_MESSAGE_PASSWORD = "비밀번호는 하나 이상의 영어, 숫자, 특수 문자를 포함하여 여덟 글자 이상으로 작성해주세요.";

    public static final String LINE_FEED = "\r\n";
    public static final String LOCAL_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String HYPHEN = "-";
}
