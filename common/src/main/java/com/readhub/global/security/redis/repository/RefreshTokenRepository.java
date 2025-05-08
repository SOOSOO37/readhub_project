package com.readhub.global.security.redis.repository;

import com.readhub.global.security.redis.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository  extends CrudRepository<RefreshToken, String> {
}
