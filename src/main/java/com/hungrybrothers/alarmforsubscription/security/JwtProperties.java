package com.hungrybrothers.alarmforsubscription.security;

public class JwtProperties {
	public static final Long TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 30;
	public static final String KEY_USER_ID = "userId";
	public static final String KEY_ROLES = "role";

	public static final String REQUEST_HEADER_AUTHORIZATION = "Authorization";
	public static final String REQUEST_HEADER_AUTHORIZATION_TYPE = "Bearer ";
}
