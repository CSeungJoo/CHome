package kr.cseungjoo.chome_be.shared.adapter.idempotency;

import kr.cseungjoo.chome_be.hub.port.in.SendHubCommandCommand;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {

    private final StringRedisTemplate redisTemplate;

    @Around("@annotation(idempotent)")
    public Object check(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String key = buildKey(idempotent.prefix(), joinPoint.getArgs());
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", Duration.ofMillis(idempotent.ttlMillis()));

        if (acquired == null || !acquired) {
            throw new DuplicateRequestException("요청이 너무 빠릅니다. 잠시 후 다시 시도해주세요.");
        }

        return joinPoint.proceed();
    }

    private String buildKey(String prefix, Object[] args) {
        for (Object arg : args) {
            if (arg instanceof SendHubCommandCommand cmd) {
                return prefix + ":" + cmd.userId() + ":" + cmd.hubId() + ":" + cmd.type();
            }
        }

        StringBuilder sb = new StringBuilder(prefix);
        for (Object arg : args) {
            sb.append(":").append(arg);
        }
        return sb.toString();
    }
}
