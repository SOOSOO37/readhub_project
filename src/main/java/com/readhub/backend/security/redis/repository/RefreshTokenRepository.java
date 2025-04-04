package com.readhub.backend.security.redis.repository;

import com.readhub.backend.security.redis.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository  extends CrudRepository<RefreshToken, String> {
}
