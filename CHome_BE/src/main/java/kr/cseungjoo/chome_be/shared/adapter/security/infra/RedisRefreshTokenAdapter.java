package kr.cseungjoo.chome_be.shared.adapter.security.infra;

import kr.cseungjoo.chome_be.shared.port.out.RefreshTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisRefreshTokenAdapter implements RefreshTokenPort {

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final String USER_TOKENS_PREFIX = "user:tokens:";

    private final StringRedisTemplate redisTemplate;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    @Override
    public String issue(Long userId) {
        String refreshToken = UUID.randomUUID().toString();
        Duration ttl = Duration.ofMillis(refreshExpirationMs);

        // 토큰 → userId 매핑 저장
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + refreshToken,
                String.valueOf(userId),
                ttl
        );

        // userId → 토큰 목록에 추가 (나중에 전체 revoke용)
        redisTemplate.opsForSet().add(USER_TOKENS_PREFIX + userId, refreshToken);
        redisTemplate.expire(USER_TOKENS_PREFIX + userId, ttl);

        return refreshToken;
    }

    @Override
    public Optional<Long> resolve(String refreshToken) {
        String userId = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + refreshToken);

        if (userId == null) {
            return Optional.empty();
        }

        return Optional.of(Long.parseLong(userId));
    }

    @Override
    public void revoke(String refreshToken) {
        String userId = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + refreshToken);

        if (userId != null) {
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + refreshToken);
            redisTemplate.opsForSet().remove(USER_TOKENS_PREFIX + userId, refreshToken);
        }
    }

    @Override
    public void revokeAllByUserId(Long userId) {
        Set<String> tokens = redisTemplate.opsForSet().members(USER_TOKENS_PREFIX + userId);

        if (tokens != null) {
            for (String token : tokens) {
                redisTemplate.delete(REFRESH_TOKEN_PREFIX + token);
            }
        }

        redisTemplate.delete(USER_TOKENS_PREFIX + userId);
    }
}
