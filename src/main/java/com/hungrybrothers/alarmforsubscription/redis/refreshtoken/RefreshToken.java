package com.hungrybrothers.alarmforsubscription.redis.refreshtoken;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@RedisHash(value = "refreshToken", timeToLive = 1000L * 60 * 60 * 24 * 90)
public class RefreshToken {
	@Id
	private String userId;

	private String refreshToken;
}
