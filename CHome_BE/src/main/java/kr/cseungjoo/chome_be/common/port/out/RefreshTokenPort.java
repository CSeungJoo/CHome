package kr.cseungjoo.chome_be.common.port.out;

import java.util.Optional;

public interface RefreshTokenPort {
    String issue(Long userId);
    Optional<Long> resolve(String refreshToken);
    void revoke(String refreshToken);
    void revokeAllByUserId(Long userId);
}
