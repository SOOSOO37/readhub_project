package com.readhub.backend.security.redis.repository;

import com.readhub.backend.security.redis.entity.LogoutAccessToken;
import org.springframework.data.repository.CrudRepository;

public interface LogoutAccessTokenRedisRepository extends CrudRepository<LogoutAccessToken, String> {
}
