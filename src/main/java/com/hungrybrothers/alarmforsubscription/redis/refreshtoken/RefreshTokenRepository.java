package com.hungrybrothers.alarmforsubscription.redis.refreshtoken;

import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
