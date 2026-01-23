package kr.cseungjoo.chome_be.auth.application.service;

import kr.cseungjoo.chome_be.auth.application.command.RefreshCommand;
import kr.cseungjoo.chome_be.auth.application.exception.InvalidRefreshTokenException;
import kr.cseungjoo.chome_be.auth.application.port.in.RefreshUseCase;
import kr.cseungjoo.chome_be.auth.application.result.RefreshResult;
import kr.cseungjoo.chome_be.global.security.port.RefreshTokenPort;
import kr.cseungjoo.chome_be.global.security.port.TokenProvider;
import kr.cseungjoo.chome_be.user.application.exception.UserNotFoundException;
import kr.cseungjoo.chome_be.user.application.port.out.UserRepositoryPort;
import kr.cseungjoo.chome_be.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshService implements RefreshUseCase {

    private final RefreshTokenPort refreshTokenPort;
    private final TokenProvider tokenProvider;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public RefreshResult execute(RefreshCommand command) {
        Long userId = refreshTokenPort.resolve(command.refreshToken())
                .orElseThrow(InvalidRefreshTokenException::new);

        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 기존 refresh token 폐기 (Rotation)
        refreshTokenPort.revoke(command.refreshToken());

        // 새 토큰 쌍 발급
        String newAccessToken = tokenProvider.issue(userId, user.getRole());
        String newRefreshToken = refreshTokenPort.issue(userId);

        return new RefreshResult(newAccessToken, newRefreshToken);
    }
}
