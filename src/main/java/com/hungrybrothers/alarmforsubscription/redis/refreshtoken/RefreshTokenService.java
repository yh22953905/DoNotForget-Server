package com.hungrybrothers.alarmforsubscription.redis.refreshtoken;

import org.springframework.stereotype.Service;

import com.hungrybrothers.alarmforsubscription.account.Account;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
	private final RefreshTokenRepository refreshTokenRepository;

	public RefreshToken saveRefreshToken(String refreshToken, Account account) {
		return refreshTokenRepository.save(RefreshToken.builder()
			.userId(account.getUserId())
			.refreshToken(refreshToken)
			.build());
	}
}
