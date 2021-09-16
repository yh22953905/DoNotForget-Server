package com.hungrybrothers.alarmforsubscription.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProperties {
	public static final Long JWT_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 30;
	public static final Long REFRESH_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 90;
	public static final String KEY_USER_ID = "userId";
	public static final String KEY_ROLES = "roles";

	public static final String REQUEST_HEADER_AUTHORIZATION = "Authorization";
	public static final String REQUEST_HEADER_AUTHORIZATION_TYPE = "Bearer ";

	public static String secretKey;

	@Value("${jwt.secret}")
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
}
